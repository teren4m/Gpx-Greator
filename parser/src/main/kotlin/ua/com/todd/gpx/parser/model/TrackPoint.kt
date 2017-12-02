package ua.com.todd.gpx.parser.model

class TrackPoint private constructor(builder: Builder) : Point(builder) {

    class Builder : Point.Builder() {

        override fun build(): TrackPoint {
            return TrackPoint(this)
        }
    }

}
