# Slim3-lite

本当ならもう使いたくないが、稼働中システムでslim3を使ってるので、ひっそりとslim3をメンテする場所
当面の目標はslim3から不要な機能をそぎ落としてメンテしやすくする

やること
- repackaged なクラスの除去

やったこと
- appengine最新バージョンへの追従
- maven/eclipseサポートの終了
- gradle/ideaサポート
- JSON 機能削除(必要ならJackson使えばええやろ)
- GWT 機能の削除(今使ってるやつおらんやろ(暴言))

# ビルド方法

```gradle
./gradlew build
```

# slim3-demo
下記コマンドを実行することですぐにデモを試すことができます
```gradle
./gradlew slim3-demo:appengineRun

```

# slim3-blank
## ローカルサーバー起動

```gradle
./gradlew slim3-blank:appengineRun

```

## コントローラーの作成

```gradle
./gradlew slim3-blank:gen-controller
./gradlew slim3-blank:gen-controller-without-view
```

## モデルの作成

```gradle
./gradlew slim3-blank:gen-model
```

## デプロイ

```gradle
gcloud config set project your_project_name
./gradlew slim3-blank:appengineDeploy

```