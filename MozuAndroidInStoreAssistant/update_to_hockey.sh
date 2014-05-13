curl \
  -F "status=2" \
  -F "notify=1" \
  -F "ipa=@app/build/apk/app-release.apk" \
  -H "X-HockeyAppToken: 80e395a551364e24a23537fe8dac8261" \
  https://rink.hockeyapp.net/api/2/apps/4fbc83986e51a19bb0c9137e06e88363/app_versions/upload
