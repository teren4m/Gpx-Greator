package ua.com.todd.gpx.parser

import org.joda.time.format.ISODateTimeFormat
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import ua.com.todd.gpx.parser.model.*
import java.io.IOException
import java.io.InputStream
import java.util.ArrayList

class Parser(val parser: XmlPullParser) {

    fun parse(gpxUrl: String) {
    }

    @Throws(XmlPullParserException::class, IOException::class)
    fun parse(inputStream: InputStream) = inputStream.use {
        parser.let {
            it.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, true)
            it.setInput(inputStream, null)
            it.nextTag()
            readGpx(it)
        }
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun readGpx(parser: XmlPullParser): Gpx {
        val wayPoints = ArrayList<WayPoint>()
        val tracks = ArrayList<Track>()
        val routes = ArrayList<Route>()

        parser.require(XmlPullParser.START_TAG, ns, TAG_GPX)
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }
            val name = parser.name
            // Starts by looking for the entry tag
            when (name) {
                TAG_WAY_POINT -> wayPoints.add(readWayPoint(parser))
                TAG_ROUTE -> routes.add(readRoute(parser))
                TAG_TRACK -> tracks.add(readTrack(parser))
                else -> skip(parser)
            }
        }
        parser.require(XmlPullParser.END_TAG, ns, TAG_GPX)
        return Gpx.Builder()
                .setWayPoints(wayPoints)
                .setRoutes(routes)
                .setTracks(tracks)
                .build()
    }

    // Parses the contents of an entry. If it encounters a title, summary, or link tag, hands them off
    // to their respective "read" methods for processing. Otherwise, skips the tag.
    @Throws(XmlPullParserException::class, IOException::class)
    private fun readTrack(parser: XmlPullParser): Track {
        val trackBuilder = Track.Builder()

        val segments = ArrayList<TrackSegment>()
        parser.require(XmlPullParser.START_TAG, ns, TAG_TRACK)
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }
            val name = parser.name
            when (name) {
                TAG_NAME -> trackBuilder.setTrackName(readName(parser))
                TAG_SEGMENT -> segments.add(readSegment(parser))
                TAG_DESC -> trackBuilder.setTrackDesc(readDesc(parser))
                TAG_CMT -> trackBuilder.setTrackCmt(readCmt(parser))
                TAG_SRC -> trackBuilder.setTrackSrc(readString(parser, TAG_SRC))
                TAG_LINK -> trackBuilder.setTrackLink(readLink(parser))
                TAG_NUMBER -> trackBuilder.setTrackNumber(readNumber(parser))
                TAG_TYPE -> trackBuilder.setTrackType(readString(parser, TAG_TYPE))
                else -> skip(parser)
            }
        }
        parser.require(XmlPullParser.END_TAG, ns, TAG_TRACK)
        return trackBuilder
                .setTrackSegments(segments)
                .build()
    }

    @Throws(IOException::class, XmlPullParserException::class)
    private fun readLink(parser: XmlPullParser): Link {
        parser.require(XmlPullParser.START_TAG, ns, TAG_LINK)

        val linkBuilder = Link.Builder()
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }
            val name = parser.name
            when (name) {
                TAG_TEXT -> linkBuilder.setLinkText(readString(parser, TAG_TEXT))
                TAG_TYPE -> linkBuilder.setLinkType(readString(parser, TAG_TYPE))
                else -> skip(parser)
            }
        }
        parser.require(XmlPullParser.END_TAG, ns, TAG_LINK)
        return linkBuilder.build()
    }

    // Processes summary tags in the feed.
    @Throws(IOException::class, XmlPullParserException::class)
    private fun readSegment(parser: XmlPullParser): TrackSegment {
        val points = ArrayList<TrackPoint>()
        parser.require(XmlPullParser.START_TAG, ns, TAG_SEGMENT)
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }
            val name = parser.name
            when (name) {
                TAG_TRACK_POINT -> points.add(readTrackPoint(parser))
                else -> skip(parser)
            }
        }
        parser.require(XmlPullParser.END_TAG, ns, TAG_SEGMENT)
        return TrackSegment.Builder()
                .setTrackPoints(points)
                .build()
    }

    /**
     * Reads a route (content of a rte tag)
     *
     * @param parser
     * @return
     * @throws IOException
     * @throws XmlPullParserException
     */
    @Throws(IOException::class, XmlPullParserException::class)
    private fun readRoute(parser: XmlPullParser): Route {
        val points = ArrayList<RoutePoint>()
        parser.require(XmlPullParser.START_TAG, ns, TAG_ROUTE)
        val routeBuilder = Route.Builder()

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }
            val name = parser.name
            when (name) {
                TAG_ROUTE_POINT -> points.add(readRoutePoint(parser))
                TAG_NAME -> routeBuilder.setRouteName(readName(parser))
                TAG_DESC -> routeBuilder.setRouteDesc(readDesc(parser))
                TAG_CMT -> routeBuilder.setRouteCmt(readCmt(parser))
                TAG_SRC -> routeBuilder.setRouteSrc(readString(parser, TAG_SRC))
                TAG_LINK -> routeBuilder.setRouteLink(readLink(parser))
                TAG_NUMBER -> routeBuilder.setRouteNumber(readNumber(parser))
                TAG_TYPE -> {
                    routeBuilder.setRouteType(readString(parser, TAG_TYPE))
                    skip(parser)
                }
                else -> skip(parser)
            }
        }
        parser.require(XmlPullParser.END_TAG, ns, TAG_ROUTE)
        return routeBuilder
                .setRoutePoints(points)
                .build()
    }

    /**
     * Reads a single point, which can either be a [TrackPoint], [RoutePoint] or [WayPoint].
     *
     * @param builder The prepared builder, one of [TrackPoint.Builder], [RoutePoint.Builder] or [WayPoint.Builder].
     * @param parser  Parser
     * @param tagName Tag name, e.g. trkpt, rtept, wpt
     */
    @Throws(XmlPullParserException::class, IOException::class)
    private fun readPoint(builder: Point.Builder, parser: XmlPullParser, tagName: String): Point {
        parser.require(XmlPullParser.START_TAG, ns, tagName)

        builder.setLatitude(java.lang.Double.valueOf(parser.getAttributeValue(null, TAG_LAT)))
        builder.setLongitude(java.lang.Double.valueOf(parser.getAttributeValue(null, TAG_LON)))

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }
            val name = parser.name
            when (name) {
                TAG_NAME -> builder.setName(readName(parser))
                TAG_ELEVATION -> builder.setElevation(readElevation(parser))
                TAG_TIME -> builder.setTime(readTime(parser))
                else -> skip(parser)
            }
        }

        parser.require(XmlPullParser.END_TAG, ns, tagName)
        return builder.build()
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun readWayPoint(parser: XmlPullParser): WayPoint {
        return readPoint(WayPoint.Builder(), parser, TAG_WAY_POINT) as WayPoint
    }

    @Throws(IOException::class, XmlPullParserException::class)
    private fun readTrackPoint(parser: XmlPullParser): TrackPoint {
        return readPoint(TrackPoint.Builder(), parser, TAG_TRACK_POINT) as TrackPoint
    }

    @Throws(IOException::class, XmlPullParserException::class)
    private fun readRoutePoint(parser: XmlPullParser): RoutePoint {
        return readPoint(RoutePoint.Builder(), parser, TAG_ROUTE_POINT) as RoutePoint
    }

    @Throws(IOException::class, XmlPullParserException::class)
    private fun readName(parser: XmlPullParser): String {
        return readString(parser, TAG_NAME)
    }

    @Throws(IOException::class, XmlPullParserException::class)
    private fun readDesc(parser: XmlPullParser): String {
        return readString(parser, TAG_DESC)
    }

    @Throws(IOException::class, XmlPullParserException::class)
    private fun readCmt(parser: XmlPullParser): String {
        return readString(parser, TAG_CMT)
    }

    @Throws(IOException::class, XmlPullParserException::class)
    private fun readString(parser: XmlPullParser, tag: String) = parser.getData(tag) {
        readText(it)
    }

    @Throws(IOException::class, XmlPullParserException::class)
    private fun readElevation(parser: XmlPullParser) = parser.getData(TAG_ELEVATION) {
        java.lang.Double.valueOf(readText(it))
    }

    @Throws(IOException::class, XmlPullParserException::class)
    private fun readTime(parser: XmlPullParser) = parser.getData(TAG_TIME) {
        ISODateTimeFormat.dateTimeParser()
                .parseDateTime(readText(it))
    }

    @Throws(IOException::class, XmlPullParserException::class)
    private fun readNumber(parser: XmlPullParser) = parser.getData(TAG_NUMBER) {
        readText(it).toInt()
    }

    fun <T> XmlPullParser.getData(tagType: String, f: (parser: XmlPullParser) -> T): T {
        this.require(XmlPullParser.START_TAG, ns, tagType)
        val data = f(this)
        this.require(XmlPullParser.END_TAG, ns, tagType)
        return data
    }

    @Throws(IOException::class, XmlPullParserException::class)
    private fun readText(parser: XmlPullParser) = parser.let {
        if (parser.next() == XmlPullParser.TEXT) {
            val result = parser.text
            parser.nextTag()
            result
        } else {
            ""
        }
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun skip(parser: XmlPullParser) = parser.apply {
        if (eventType != XmlPullParser.START_TAG) {
            throw IllegalStateException()
        }
        var depth = 1
        while (depth != 0) {
            when (parser.next()) {
                XmlPullParser.END_TAG -> depth--
                XmlPullParser.START_TAG -> depth++
            }
        }
    }

    companion object {
        private val ns: String? = null
    }
}