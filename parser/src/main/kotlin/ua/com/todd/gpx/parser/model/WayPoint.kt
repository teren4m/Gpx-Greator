package ua.com.todd.gpx.parser.model

class WayPoint private constructor(builder: Builder) : Point(builder) {

    class Builder : Point.Builder() {

        override fun build(): WayPoint {
            return WayPoint(this)
        }
    }

}
