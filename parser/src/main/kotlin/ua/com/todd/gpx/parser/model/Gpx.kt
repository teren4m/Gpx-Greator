package ua.com.todd.gpx.parser.model

import java.util.*

class Gpx private constructor(builder: Builder) {
    val wayPoints: List<WayPoint>
    val routes: List<Route>
    val tracks: List<Track>

    init {
        wayPoints = Collections.unmodifiableList(ArrayList(builder.mWayPoints!!))
        routes = Collections.unmodifiableList(ArrayList(builder.mRoutes!!))
        tracks = Collections.unmodifiableList(ArrayList(builder.mTracks!!))
    }

    class Builder {
        var mWayPoints: List<WayPoint>? = null
        var mRoutes: List<Route>? = null
        var mTracks: List<Track>? = null

        fun setTracks(tracks: List<Track>): Builder {
            mTracks = tracks
            return this
        }

        fun setWayPoints(wayPoints: List<WayPoint>): Builder {
            mWayPoints = wayPoints
            return this
        }

        fun setRoutes(routes: List<Route>): Builder {
            this.mRoutes = routes
            return this
        }

        fun build(): Gpx {
            return Gpx(this)
        }
    }
}
