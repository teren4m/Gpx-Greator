package ua.com.todd.gpx.parser.model

import java.util.*

class TrackSegment private constructor(builder: Builder) {
    val trackPoints: List<TrackPoint>

    init {
        trackPoints = Collections.unmodifiableList(ArrayList(builder.mTrackPoints!!))
    }

    class Builder {
        var mTrackPoints: List<TrackPoint>? = null

        fun setTrackPoints(trackPoints: List<TrackPoint>): Builder {
            mTrackPoints = trackPoints
            return this
        }

        fun build(): TrackSegment {
            return TrackSegment(this)
        }
    }
}
