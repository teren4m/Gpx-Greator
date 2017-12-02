package ua.com.todd.gpx.parser.model

import java.util.*

class Route private constructor(builder: Builder) {
    val routePoints: List<RoutePoint>
    val routeName: String?
    val routeDesc: String?
    val routeCmt: String?
    val routeSrc: String?
    val routeNumber: Int?
    val routeLink: Link?
    val routeType: String?

    init {
        routePoints = Collections.unmodifiableList(ArrayList(builder.mRoutePoints!!))
        routeName = builder.mRouteName
        routeDesc = builder.mRouteDesc
        routeCmt = builder.mRouteCmt
        routeSrc = builder.mRouteSrc
        routeNumber = builder.mRouteNumber
        routeLink = builder.mRouteLink
        routeType = builder.mRouteType
    }

    class Builder {
        var mRoutePoints: List<RoutePoint>? = null
        var mRouteName: String? = null
        var mRouteDesc: String? = null
        var mRouteCmt: String? = null
        var mRouteSrc: String? = null
        var mRouteNumber: Int? = null
        var mRouteLink: Link? = null
        var mRouteType: String? = null

        fun setRoutePoints(routePoints: List<RoutePoint>): Builder {
            mRoutePoints = routePoints
            return this
        }

        fun setRouteName(mRouteName: String): Builder {
            this.mRouteName = mRouteName
            return this
        }

        fun setRouteDesc(mRouteDesc: String): Builder {
            this.mRouteDesc = mRouteDesc
            return this
        }

        fun setRouteCmt(mRouteCmt: String): Builder {
            this.mRouteCmt = mRouteCmt
            return this
        }

        fun setRouteSrc(mRouteSrc: String): Builder {
            this.mRouteSrc = mRouteSrc
            return this
        }

        fun setRouteNumber(mRouteNumber: Int?): Builder {
            this.mRouteNumber = mRouteNumber
            return this
        }

        fun setRouteLink(mRouteLink: Link): Builder {
            this.mRouteLink = mRouteLink
            return this
        }

        fun setRouteType(mRouteType: String): Builder {
            this.mRouteType = mRouteType
            return this
        }

        fun build(): Route {
            return Route(this)
        }
    }
}
