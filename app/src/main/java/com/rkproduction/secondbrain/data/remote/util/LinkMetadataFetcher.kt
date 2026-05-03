package com.rkproduction.secondbrain.data.remote.util

import com.rkproduction.secondbrain.data.remote.api.LinkScraperService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import javax.inject.Inject

class LinkMetadataFetcher @Inject constructor(
    private val scraperService: LinkScraperService
) {
    data class LinkMetadata(
        val title: String?,
        val description: String?,
        val imageUrl: String?
    )

    suspend fun fetchMetadata(url: String): LinkMetadata {
        return withContext(Dispatchers.IO) {
            try {
                // 1. Fetch HTML string via Retrofit
                val responseBody = scraperService.fetchHtml(url)
                val html = responseBody.string()

                // 2. Parse with Jsoup (providing base URL for relative link resolution)
                val document = Jsoup.parse(html, url)

                // 3. Extract OpenGraph or Standard Tags
                val title = document.select("meta[property=og:title]").attr("content").ifBlank {
                    document.select("meta[name=twitter:title]").attr("content").ifBlank {
                        document.title()
                    }
                }

                val description = document.select("meta[property=og:description]").attr("content").ifBlank {
                    document.select("meta[name=description]").attr("content").ifBlank {
                        document.select("meta[name=twitter:description]").attr("content")
                    }
                }

                val imageUrl = document.select("meta[property=og:image]").attr("abs:content").ifBlank {
                    document.select("meta[name=twitter:image]").attr("abs:content").ifBlank {
                        document.select("link[rel=image_src]").attr("abs:href")
                    }
                }

                LinkMetadata(
                    title = title.takeIf { it.isNotBlank() },
                    description = description.takeIf { it.isNotBlank() },
                    imageUrl = imageUrl.takeIf { it.isNotBlank() }
                )
            } catch (e: Exception) {
                e.printStackTrace()
                LinkMetadata(null, null, null)
            }
        }
    }
}
