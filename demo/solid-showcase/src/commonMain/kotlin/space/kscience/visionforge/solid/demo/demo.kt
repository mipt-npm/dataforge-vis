package space.kscience.visionforge.solid.demo

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import space.kscience.dataforge.meta.Meta
import space.kscience.dataforge.meta.invoke
import space.kscience.dataforge.names.toName
import space.kscience.visionforge.Colors
import space.kscience.visionforge.VisionLayout
import space.kscience.visionforge.solid.*
import space.kscience.visionforge.solid.specifications.Canvas3DOptions
import space.kscience.visionforge.visible
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random


fun VisionLayout<Solid>.demo(name: String, title: String = name, block: SolidGroup.() -> Unit) {
    val meta = Meta {
        "title" put title
    }
    val vision = SolidGroup(block)
    render(name.toName(), vision)
}

val canvasOptions = Canvas3DOptions {
    size {
        minSize = 400
    }
    axes {
        size = 500.0
        visible = true
    }
    camera {
        distance = 600.0
        latitude = PI / 6
    }
}


fun VisionLayout<Solid>.showcase() {
    demo("shapes", "Basic shapes") {
        box(100.0, 100.0, 100.0) {
            z = -110.0
            color("teal")
        }
        sphere(50.0) {
            x = 110
            detail = 16
            color("red")
        }
        tube(50, height = 10, innerRadius = 25, angle = PI) {
            y = 110
            detail = 16
            rotationX = PI / 4
            color("blue")
        }
        sphereLayer(50, 40) {
            theta = (PI / 2).toFloat()
            rotationX = -PI * 3 / 4
            z = 110
            color(Colors.pink)
        }
    }

    demo("dynamic", "Dynamic properties") {
        val group = group {
            box(100, 100, 100) {
                z = 110.0
                opacity = 0.5
            }

            box(100, 100, 100) {
                visible = false
                x = 110.0
                //override color for this cube
                color(1530)

                launch(Dispatchers.Main) {
                    while (isActive) {
                        delay(500)
                        visible = !(visible ?: false)
                    }
                }
            }
        }

        launch(Dispatchers.Main) {
            val random = Random(111)
            while (isActive) {
                delay(1000)
                group.color(random.nextInt(0, Int.MAX_VALUE))
            }
        }
    }

    demo("rotation", "Rotations") {
        box(100, 100, 100)
        group {
            x = 200
            rotationY = PI / 4
            box(100, 100, 100) {
                rotationZ = PI / 4
                color(Colors.red)
            }
        }
    }

    demo("extrude", "extruded shape") {
        extrude {
            shape {
                polygon(8, 50)
            }
            for (i in 0..100) {
                layer(i * 5, 20 * sin(2 * PI / 100 * i), 20 * cos(2 * PI / 100 * i))
            }
            color(Colors.teal)
            rotationX = -PI / 2
        }
    }

    demo("lines", "Track / line segments") {
        sphere(100) {
            detail = 32
            opacity = 0.4
            color(Colors.blue)
        }
        repeat(20) {
            polyline(Point3D(100, 100, 100), Point3D(-100, -100, -100)) {
                thickness = 3.0
                rotationX = it * PI2 / 20
                color(Colors.green)
                //rotationY = it * PI2 / 20
            }
        }
    }

    demo("text", "Box with a label") {
        box(100, 100, 50) {
            opacity = 0.3
        }
        label("Hello, world!", fontSize = 12) {
            z = 26
        }
    }
}

fun VisionLayout<Solid>.showcaseCSG() {
    demo("CSG.simple", "CSG operations") {
        composite(CompositeType.INTERSECT) {
            y = 300
            box(100, 100, 100) {
                z = 50
            }
            sphere(50) {
                detail = 32
            }
            material {
                color(Colors.pink)
            }
        }
        composite(CompositeType.UNION) {
            box(100, 100, 100) {
                z = 50
            }
            sphere(50) {
                detail = 32
            }
            color("lightgreen")
            opacity = 0.7
        }
        composite(CompositeType.SUBTRACT) {
            y = -300
            box(100, 100, 100) {
                z = 50
            }
            sphere(50) {
                detail = 32
            }
            color("teal")
            opacity = 0.7
        }
    }

    demo("CSG.custom", "CSG with manually created object") {
        intersect {
            cylinder(60, 10) {
                detail = 32
            }
            box(100, 100, 100)
            color("red")
            opacity = 0.5
        }
    }
}