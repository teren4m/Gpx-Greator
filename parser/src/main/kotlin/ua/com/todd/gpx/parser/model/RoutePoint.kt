package ua.com.todd.gpx.parser.model

class RoutePoint private constructor(builder: Builder) : Point(builder) {

    class Builder : Point.Builder() {

        override fun build(): RoutePoint {
            return RoutePoint(this)
        }
    }

}
