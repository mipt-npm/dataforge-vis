package hep.dataforge.vis.spatial.fx

import hep.dataforge.vis.spatial.specifications.CameraSpec
import javafx.beans.InvalidationListener
import javafx.beans.property.SimpleDoubleProperty
import javafx.event.EventHandler
import javafx.geometry.Point3D
import javafx.scene.Camera
import javafx.scene.Node
import javafx.scene.SubScene
import javafx.scene.input.MouseEvent
import javafx.scene.input.ScrollEvent
import javafx.scene.shape.Sphere
import javafx.scene.transform.Rotate
import javafx.scene.transform.Translate
import tornadofx.*
import kotlin.math.*


class OrbitControls internal constructor(camera: Camera, canvas: SubScene, spec: CameraSpec) {

    val distanceProperty = SimpleDoubleProperty(spec.distance)
    var distance by distanceProperty

    val azimuthProperty = SimpleDoubleProperty(spec.azimuth)
    var azimuth by azimuthProperty

    val zenithProperty = SimpleDoubleProperty(PI/2 - spec.latitude)
    var zenith by zenithProperty

    val latitudeProperty = zenithProperty.unaryMinus().plus(PI/2)
    val latitude by latitudeProperty

    val baseXProperty = SimpleDoubleProperty(0.0)
    var x by baseXProperty
    val baseYProperty = SimpleDoubleProperty(0.0)
    var y by baseYProperty
    val baseZProperty = SimpleDoubleProperty(0.0)
    var z by baseZProperty

//    val basePositionProperty: ObjectBinding<Point3D> =
//        nonNullObjectBinding(baseXProperty, baseYProperty, baseZProperty) {
//            Point3D(x, y, z)
//        }
//
//    val basePosition by basePositionProperty

    val centerMarker by lazy {
        Sphere(10.0).also {
            it.translateXProperty().bind(baseXProperty)
            it.translateYProperty().bind(baseYProperty)
            it.translateZProperty().bind(baseZProperty)
        }
    }

    private val rx = Rotate(0.0, Rotate.X_AXIS)

    private val ry = Rotate(0.0, Rotate.Y_AXIS)

    private val translate = Translate()

    private val rz = Rotate(180.0, Rotate.Z_AXIS)


    init {
        camera.transforms.setAll(ry, rx, translate,rz)
        update()
        val listener = InvalidationListener {
            update()
        }
        distanceProperty.addListener(listener)
        azimuthProperty.addListener(listener)
        zenithProperty.addListener(listener)
        baseXProperty.addListener(listener)
        baseYProperty.addListener(listener)
        baseZProperty.addListener(listener)

        canvas.apply {
            camera.translateXProperty().bind(widthProperty().divide(2))
            camera.translateZProperty().bind(heightProperty().divide(2))
            handleMouse()
        }
//        coordinateContainer?.vbox {
//            label(distanceProperty.asString())
//            label(azimuthProperty.asString())
//            label(zenithProperty.asString())
//        }
    }

    private fun update() {
        val spherePosition = Point3D(
            sin(zenith) * sin(azimuth),
            cos(zenith),
            sin(zenith) * cos(azimuth)
        ).times(distance)
        val basePosition = Point3D(x, y, z)
        //Create direction vector
        val cameraPosition = basePosition + spherePosition
        val camDirection: Point3D = (-spherePosition).normalize()

        val xRotation = Math.toDegrees(asin(-camDirection.y))
        val yRotation = Math.toDegrees(atan2(camDirection.x, camDirection.z))

        rx.pivotX = cameraPosition.x
        rx.pivotY = cameraPosition.y
        rx.pivotZ = cameraPosition.z
        rx.angle = xRotation

        ry.pivotX = cameraPosition.x
        ry.pivotY = cameraPosition.y
        ry.pivotZ = cameraPosition.z
        ry.angle = yRotation

        translate.x = cameraPosition.x
        translate.y = cameraPosition.y
        translate.z = cameraPosition.z
    }


    private fun Node.handleMouse() {

        var mousePosX = 0.0
        var mousePosY = 0.0
        var mouseOldX: Double
        var mouseOldY: Double
        var mouseDeltaX: Double
        var mouseDeltaY: Double

        onMousePressed = EventHandler<MouseEvent> { me ->
            mousePosX = me.sceneX
            mousePosY = me.sceneY
            mouseOldX = me.sceneX
            mouseOldY = me.sceneY
        }

        onMouseDragged = EventHandler<MouseEvent> { me ->
            mouseOldX = mousePosX
            mouseOldY = mousePosY
            mousePosX = me.sceneX
            mousePosY = me.sceneY
            mouseDeltaX = mousePosX - mouseOldX
            mouseDeltaY = mousePosY - mouseOldY

            val modifier = when {
                me.isControlDown -> CONTROL_MULTIPLIER
                me.isShiftDown -> SHIFT_MULTIPLIER
                else -> 1.0
            }

            if (me.isPrimaryButtonDown) {
                azimuth -= mouseDeltaX * MOUSE_SPEED * modifier * ROTATION_SPEED
                zenith -= mouseDeltaY * MOUSE_SPEED * modifier * ROTATION_SPEED
            } else if (me.isSecondaryButtonDown) {
                x += mouseDeltaX * MOUSE_SPEED * modifier * TRACK_SPEED
                z -= mouseDeltaY * MOUSE_SPEED * modifier * TRACK_SPEED
            }
        }
        onScroll = EventHandler<ScrollEvent> { event ->
            distance = max(1.0, distance - MOUSE_SPEED * event.deltaY * RESIZE_SPEED)
        }
    }

    companion object {
        private const val CONTROL_MULTIPLIER = 0.1
        private const val SHIFT_MULTIPLIER = 10.0
        private const val MOUSE_SPEED = 0.1
        private const val ROTATION_SPEED = 0.02
        private const val TRACK_SPEED = 6.0
        private const val RESIZE_SPEED = 10.0
    }
}

fun Camera.orbitControls(canvas: SubScene, spec: CameraSpec) =
    OrbitControls(this, canvas, spec)