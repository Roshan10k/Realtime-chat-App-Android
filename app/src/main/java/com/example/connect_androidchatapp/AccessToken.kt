package com.example.connect_androidchatapp

import android.os.Handler
import android.os.Looper
import com.google.auth.oauth2.GoogleCredentials
import java.io.ByteArrayInputStream
import java.io.IOException
import java.nio.charset.StandardCharsets

object AccessToken {
    private val firebaseMessagingScope = "https://www.googleapis.com/auth/firebase.messaging"
    private var cachedToken: String? = null

    // Add a method to get token asynchronously
    fun getAccessTokenAsync(callback: (String?) -> Unit) {
        Thread {
            try {
                val token = getAccessTokenInternal()
                cachedToken = token

                // Return result on main thread
                Handler(Looper.getMainLooper()).post {
                    callback(token)
                }
            } catch (e: IOException) {
                Handler(Looper.getMainLooper()).post {
                    callback(null)
                }
            }
        }.start()
    }

    // Renamed original method for internal use
    private fun getAccessTokenInternal(): String? {
        try {
            val jsonString = "{\n" +
                    "  \"type\": \"service_account\",\n" +
                    "  \"project_id\": \"connect-chat-app-c64d3\",\n" +
                    "  \"private_key_id\": \"991d910603501aea375775e14236d1ef97dcead0\",\n" +
                    "  \"private_key\": \"-----BEGIN PRIVATE KEY-----\\nMIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQCoo9GhK39cqNCb\\nWs3QeV2Dz/FH2Zed9V1nkbL7XDwEHH8aI0XLizpkSyIxln8AdQpXGrKtucELkXH/\\na9ABx9x8hmIrKtSBHFuFo1Tev9N6Y2cXwvWXwrx3weL+lPkzaxWJPKXkRowG+WEP\\nRfI9190hy5Q21mfIFavOKJ4p9NlaXiFiJP5+9+E6Dkn11QQMTqzgh+a0k3KyTWaJ\\nIjtBuvhL5SA+i42FBVIukjl4IpyVgqRaruXO4P5NAhfRxtA5IwrRX2Pp0OW7xGtn\\n7rLdofSlgu++NIsR5aihBuEDD2Xr/uc44Y/42BeztOMcPbiEbI+Fy0Lc6qElVnCD\\nmV998YkNAgMBAAECggEANKbNBOptPdNRKif8GUpDKavLM7kabs1UJ8lkHbmK3We+\\nQT1hBa9HAFg66UuJBG4GV/K0dawIuyFie7JAA8ZQLyrPzJV3OoZNhthbX555mv0d\\nC1ypzAOXTzagNgbyFR0qYKooSzDBeE31AU0/2BIk7u++Ky7ewSgO93NUClpxYBaA\\ny+pNAyVLidCWzEJkvQLmMTsadRxyRLAyj+DHQbys02HAb8otk8x+x5p+g8sdGkmG\\nNi1TpkWUf2TL/QjIW2YQ5WkxLl813wwICuD0TKOu5OqDffRaNIaPiJtGpSm6vONt\\nI8+mt+6OxcbDLh8EM8bfVxU5c8P/+n6GnG2g4P45mwKBgQDQuYVz4oN5w3dwcijs\\nTHDjbo2PfBcrWS5R3brHrWgrddzwZzVj4YuMiZpoC1MruypP6O+fZJUg8R9UKLlm\\nGSei777XcXQzN389iQ5k8+aexh4Kiz2AIu3PLYNsiEz3exMfZtlvBMzVbkvxlPQg\\nleBr4OHvbX1zsCVC8kOSfH8vjwKBgQDO1hAjR+5GQcI5lZLR6Z8ArLvIJrbgfBp6\\nzfsogVA41tNSmNdX79sz7I3lWhkaODpvInme3NWd87TuPQn4E22MiJOsZfDIyJh9\\npmmoTlXbGRW7L8BlySsOtBfMYOGl+i/MQkxb1jHfWKoVh/xTbcSFG5fDqWkjvioJ\\n+to8P5UvowKBgFdL4IbxS+/XUtMSzA7YgbZ9fHNwgPg+WmMfC4mL+y+CIh/h6rCV\\nmpnyy0OMyQJXS4EUeXP+y2h1sJawWapcBvncr8LHzsPFCFCiTATpBaGOVu4uAlDf\\nSvsFnBv+fcJcFUli6LnIFiuF7/lgRSKDzNMRwS00a+3A1DTFwoE31LH9AoGASA7r\\nsF7u9CsUr1scu2TYsDN9eE2EiNCzqPT5+zKR8Dtng+8bltdJVt2i0bSB6lCMiwtH\\ns8Db6xtsfVNmlbavA7bRO86IBGteNOg1Bx3Da4FhwMTyEoPzLDwxzlIyStDOtiqi\\nz5vf5B/PoZCup6ImRPlH9jKfHoSxwKhUP4knQGECgYAyKQX51baF7eFcbsoaFX3H\\nXzICwOd2kS99UGaNvWHMcjYQyVxPfCtJi24VVv7inWrTXF4kAp9wxzqg5wCgOXii\\nxqbnW6HbCk8WB8g26e9Kk5Z32Go/0aMldXYj2f3Hqb2v6tapul00+rKmNvWnzQoX\\n6NIMRP7AnTapm3h/aX7YAg==\\n-----END PRIVATE KEY-----\\n\",\n" +
                    "  \"client_email\": \"firebase-adminsdk-fbsvc@connect-chat-app-c64d3.iam.gserviceaccount.com\",\n" +
                    "  \"client_id\": \"108498452645696753074\",\n" +
                    "  \"auth_uri\": \"https://accounts.google.com/o/oauth2/auth\",\n" +
                    "  \"token_uri\": \"https://oauth2.googleapis.com/token\",\n" +
                    "  \"auth_provider_x509_cert_url\": \"https://www.googleapis.com/oauth2/v1/certs\",\n" +
                    "  \"client_x509_cert_url\": \"https://www.googleapis.com/robot/v1/metadata/x509/firebase-adminsdk-fbsvc%40connect-chat-app-c64d3.iam.gserviceaccount.com\",\n" +
                    "  \"universe_domain\": \"googleapis.com\"\n" +
                    "}\n"

            val stream = ByteArrayInputStream(jsonString.toByteArray(StandardCharsets.UTF_8))
            val googleCredential = GoogleCredentials.fromStream(stream)
                .createScoped(arrayListOf(firebaseMessagingScope))

            googleCredential.refresh()

            return googleCredential.accessToken.tokenValue
        } catch (e: IOException) {
            return null
        }
    }

    // Keep this for backward compatibility but with caching
    fun getAccessToken(): String? {
        return cachedToken
    }
}

