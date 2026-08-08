# GCP Setup — 分担表

GCP セットアップで「ユーザー本人にしかできないこと」と「Claude が gcloud / gh で代行できること」の整理。kei-1111.github.io の既存インフラ(GCP プロジェクト、WIF プール、Artifact Registry)を流用する前提。

## 0. 事前に決めること(ユーザー)

| 決定事項 | 推奨 / 備考 |
|---|---|
| GCP プロジェクト | ポートフォリオの server が動いている既存プロジェクトに同居(WIF・Artifact Registry を流用でき設定が最小)。プロジェクト ID を伝える |
| リージョン | ポートフォリオと同じ(おそらく asia-northeast1) |
| GCS バケット名 | グローバル一意。例: `kei-1111-portfolio-content` |
| 管理者アカウント | 管理画面へのログインを許可する Google アカウント(メールアドレス 1 件) |

## 1. ユーザー本人にしかできない操作

1. **gcloud 認証** — セッション内で `! gcloud auth login` を実行(ブラウザ認証)。続けて `! gcloud config set project <PROJECT_ID>`。以降の gcloud 操作は Claude が承認ベースで代行できる。
2. **OAuth 同意画面の設定**(Console 手作業、CLI 不可)— APIs & Services → OAuth consent screen。個人 Gmail のため User Type は **External**、Test users に自分を追加(公開申請は不要)。
3. **OAuth 2.0 クライアント ID(Web application)の作成**(Console 手作業)— Credentials → Create Credentials。承認済み JavaScript 生成元に **Cloud Run の URL**(初回デプロイ後に確定)と、ローカル開発用 `http://localhost:8082` を登録。できたクライアント ID を共有する(Google Identity Services の ID トークンフローなのでクライアントシークレットは使わない)。
   - Cloud Run URL はデプロイ後に確定するため、「先にローカル origin だけで作成 → デプロイ後に URL を追記」の 2 段階になる。

## 2. Claude が代行できること(gcloud auth 後、承認しながら)

- API 有効化: `run` / `storage` / `iamcredentials` / `artifactregistry`
- GCS バケット作成(非公開、uniform access)
- サービスアカウント作成 + IAM 設定
  - admin server 実行 SA: バケットへの `roles/storage.objectAdmin`
  - ポートフォリオ server 実行 SA: 同バケットへの `roles/storage.objectViewer`
- WIF: 既存プール/プロバイダに `kei-1111/kei-1111-admin` リポジトリを許可し、デプロイ SA をバインド
- `server/Dockerfile` と `deploy-server.yml` の作成(本家の構成を流用、fat jar は `-PbundleWebApp` で UI 同梱)
- GitHub 側 secrets/vars 設定(`gh secret set` / `gh variable set`): `GCP_WORKLOAD_IDENTITY_PROVIDER` / `GCP_SERVICE_ACCOUNT` / `GCP_PROJECT_ID` / `GCP_REGION` / `GAR_REPOSITORY`
- ID トークン検証(audience = OAuth クライアント ID + メール許可リスト)のサーバー実装と、UI 側の Google Sign-In 組み込み

## 3. 進行順序

1. ユーザー: gcloud auth login + プロジェクト決定 (§1-1)
2. Claude: API / バケット / SA / WIF / Dockerfile / deploy-server.yml / secrets (§2)
3. 初回デプロイ → Cloud Run URL 確定
4. ユーザー: OAuth 同意画面 + クライアント ID 作成、URL を origins に登録 (§1-2, 1-3)
5. Claude: 認証実装 → 画像/文言 CRUD 実装

## 費用感

Cloud Run は scale-to-zero(既存の max-instances=1 方式を踏襲)、GCS はポートフォリオ規模のアセットなら月数円程度。既存プロジェクト同居なら新たな固定費は発生しない。
