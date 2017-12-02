package ua.com.todd.gpx.parser.model

import org.joda.time.DateTime

abstract class Point internal constructor(builder: Builder) {
    val latitude: Double?
    val longitude: Double?
    val elevation: Double?
    val time: DateTime?
    val name: String?

    init {
        latitude = builder.mLatitude
        longitude = builder.mLongitude
        elevation = builder.mElevation
        time = builder.mTime
        name = builder.mName
    }

    abstract class Builder {
        var mLatitude: Double? = null
        var mLongitude: Double? = null
        var mElevation: Double? = null
        var mTime: DateTime? = null
        var mName: String? = null

        fun setLatitude(latitude: Double?): Builder {
            mLatitude = latitude
            return this
        }

        fun setLongitude(longitude: Double?): Builder {
            mLongitude = longitude
            return this
        }

        fun setElevation(elevation: Double?): Builder {
            mElevation = elevation
            return this
        }

        fun setTime(time: DateTime): Builder {
            mTime = time
            return this
        }

        fun setName(mName: String): Builder {
            this.mName = mName
            return this
        }

        abstract fun build(): Point
    }
}
