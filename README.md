# stream.mattes.io
[YouTube Channel](https://www.youtube.com/channel/UCU5cBbPkc-7_6HIHIbCaCYA)

Spring Boot and Angular app to expose unlisted YouTube streams on your own terms on your own server.

## run in dummy mode
run `./gradlew bootRun` to try it out with dummy data

## run against YouTube API
1. enable the youtube API:
set `app.youtube.enabled` to `true` in the application.yaml
1. configure Google OAuth 2.0 credentials:
in the application.yaml, set the properties `app.youtube.client_id` and `app.youtube.client_secret`
1. configure a refresh token:
run the app with `./gradlew bootRun` and visit http://localhost:8080/setup
to get an Authentication Code.<br>
With that Auth Code go to http://localhost:8080/setup?authCode=<auth_code> to get the refresh token.<br/>
Then set the `app.youtube.refresh_token` property in the application.yaml accordingly.


From then on, you can run the app via `./gradlew bootRun -Pargs=--app.youtube.enabled=true,--app.youtube.refresh_token=<token>` 
which will disable the setup endpoint and enable the YouTube API to retrieve active and completed streams.