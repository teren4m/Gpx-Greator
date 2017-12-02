package ua.com.todd.gpx.parser.model

class Link private constructor(builder: Builder) {

    val text: String?
    val type: String?

    init {
        text = builder.mLinkText
        type = builder.mLinkType
    }

    class Builder {
        var mLinkText: String? = null
        var mLinkType: String? = null

        fun setLinkText(linkText: String): Builder {
            mLinkText = linkText
            return this
        }

        fun setLinkType(linkType: String): Builder {
            mLinkType = linkType
            return this
        }

        fun build(): Link {
            return Link(this)
        }
    }
}
