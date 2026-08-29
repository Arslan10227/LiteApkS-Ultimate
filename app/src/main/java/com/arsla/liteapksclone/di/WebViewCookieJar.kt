package com.arsla.liteapksclone.di

import android.webkit.CookieManager
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl

class WebViewCookieJar : CookieJar {

    private val cookieManager: CookieManager = CookieManager.getInstance()

    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        for (cookie in cookies) {
            cookieManager.setCookie(url.toString(), "${cookie.name}=${cookie.value}")
        }
        cookieManager.flush()
    }

    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        val cookieString = cookieManager.getCookie(url.toString()) ?: return emptyList()
        val host = url.host()
        val path = url.encodedPath().ifEmpty { "/" }

        return cookieString.split(";").mapNotNull { part ->
            val trimmed = part.trim()
            val eq = trimmed.indexOf('=')
            if (eq <= 0) return@mapNotNull null

            val name = trimmed.substring(0, eq)
            val value = trimmed.substring(eq + 1)

            Cookie.Builder()
                .name(name)
                .value(value)
                .domain(host)
                .path(path)
                .expiresAt(Long.MAX_VALUE)
                .build()
        }
    }
}
