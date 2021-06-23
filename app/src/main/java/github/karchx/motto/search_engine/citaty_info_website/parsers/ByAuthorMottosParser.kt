package github.karchx.motto.search_engine.citaty_info_website.parsers

import github.karchx.motto.search_engine.citaty_info_website.data.Author
import github.karchx.motto.search_engine.citaty_info_website.data.Motto
import github.karchx.motto.model.storages.Constants
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements

class ByAuthorMottosParser(private var author: Author) : MottosParser {

    override fun getMottos(quantityMottos: Int): ArrayList<Motto> {
        val mottos = ArrayList<Motto>()
        val uriToParse = getUriToParse(author)

        val okHttp = OkHttpClient()
        val request: Request = Request.Builder().url(uriToParse).get().build()
        val doc: Document = Jsoup.parse(okHttp.newCall(request).execute().body!!.string())
        val articles: Elements = doc.select(Constants.ARTICLE_ROOT_ELEMENT_NAME)

        val limitedQuantityMottos = getLimitedMottosQuantity(articles, quantityMottos)

        for (mottoIndex in 0 until limitedQuantityMottos) {
            try {
                mottos.add(HtmlMottosParser.getMottoFromHtml(doc, mottoIndex))
            } catch (ex: Exception) {
            }
        }
        return mottos
    }

    private fun getLimitedMottosQuantity(articles: Elements, quantityMottos: Int): Int {
        return if (articles.size < quantityMottos) articles.size
        else quantityMottos
    }

    private fun getUriToParse(author: Author): String {
        val baseUri = Constants.DOMAIN
        val topicUri = author.pageUri
        val sortType = Constants.MOTTOS_SORT_TYPE
        return "$baseUri$topicUri$sortType"
    }
}
