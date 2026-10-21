# Google Play releases

Pushing a version tag runs `.github/workflows/release.yml` on the self-hosted Android runner. It builds the Google release AAB, signs it with the upload key, saves the AAB and R8 mapping as a GitHub artifact, and publishes to **internal testing** for `com.imecatro.demosales`.

## GitHub secrets

Configure these repository secrets before pushing a release tag:

| Secret | Value |
| --- | --- |
| `ANDROID_KEYSTORE_BASE64` | Upload keystore encoded as Base64. |
| `ANDROID_KEYSTORE_PASSWORD` | Keystore password. |
| `ANDROID_KEY_ALIAS` | Alias of the upload key. |
| `ANDROID_KEY_PASSWORD` | Password of that key; it can match the keystore password. |
| `PLAY_SERVICE_ACCOUNT_JSON` | The service account JSON key, as plain JSON. |
| `GOOGLE_SERVICES_JSON_BASE64` | Google flavor Firebase configuration encoded as Base64; also used by Android CI. |
| `MAPS_API_KEY` | Google Maps API key, if maps are used in this release. The existing `local.properties` value remains the fallback for local builds. |

The service account needs access to this app and permission to release to testing tracks in Google Play Console. Enable the Google Play Developer API in its Google Cloud project. The upload certificate must match the one registered for this app in Play Console.

See [Google Play API setup](https://developers.google.com/android-publisher/getting_started) and [Android app signing](https://developer.android.com/studio/publish/app-signing).

## Create a release

1. Update `versionName` and increment `versionCode` in `app/build.gradle.kts`. The code must be greater than previously published versions; creating a tag does not increment it.
2. Commit the changes, including the release workflow, and push them to GitHub.
3. Push a tag matching `versionName`. For example, after setting `versionName = "1.15.4"`:

   ```sh
   git tag -a v1.15.4 -m "Release 1.15.4"
   git push origin v1.15.4
   ```

The workflow checks the tag against `versionName` before building. Tags use `vMAJOR.MINOR.PATCH`, optionally followed by a prerelease suffix that also appears in `versionName`.

Open **Actions → Google Play Internal Release** to follow the upload and download the `google-play-<tag>` artifact. The release is assigned to the internal testing track with status `completed`; distribution remains subject to Play Console requirements and any Google review. Configure the internal tester list and share its opt-in link from Play Console.

Google Play rejects reused version codes, including when rerunning a release that already uploaded successfully. A rerun is not a way to publish a different binary with the same version code.

## Signing behavior

Release signing reads `ANDROID_KEYSTORE_PATH`, `ANDROID_KEYSTORE_PASSWORD`, `ANDROID_KEY_ALIAS`, and `ANDROID_KEY_PASSWORD` from the build environment. The workflow restores the keystore and service account key into the runner's temporary directory and deletes both and the generated Firebase configuration when the job finishes. Configuration caching is disabled for this build so signing credentials are not stored in that cache.

Without the signing environment variables, release builds are unsigned; debug builds continue using their usual debug signing. Local signed releases can use the same environment variables or Android Studio's signed bundle flow.
