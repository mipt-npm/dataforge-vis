/*
 * The MIT License
 *
 * Copyright 2017-2018 Lars Ivar Hatledal
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING  FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

@file:JsModule("three")
@file:JsNonModule

package info.laht.threekt.materials

open external class ShaderMaterial : Material {

    var defines: dynamic
    var uniforms: dynamic

    var vertexShader: String
    var fragmentShader: String
    var linewidth: Double

    var wireframe: Boolean
    var wireframeLinewidth: Double

    var clipping: Boolean

    var skinning: Boolean
    var morphTargets: Boolean
    var morphNormals: Boolean


    interface Extensions {
        var derivatives: Boolean
        var fragDepth: Boolean
        var drawBuffers: Boolean
        var shaderTextureLOD: Boolean
    }


    var extensions: Extensions

    var index0AttributeName: String

    override fun clone(): ShaderMaterial
    fun copy(material: ShaderMaterial): ShaderMaterial


}