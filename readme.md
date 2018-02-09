# Slim3-lite

本当ならもう使いたくないが、稼働中システムでslim3を使ってるので、ひっそりとslim3をメンテする場所
当面の目標はslim3から不要な機能をそぎ落としてメンテしやすくする

やること
- appengine最新バージョンへの追従
- repackaged なクラスの除去
- JSON 機能削除
- GWT 機能の削除
- maven/eclipseサポートの終了
- gradle/ideaサポート

# ビルド方法

```gradle
./gradlew build
```

# slim3-blankプロジェクトについて
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