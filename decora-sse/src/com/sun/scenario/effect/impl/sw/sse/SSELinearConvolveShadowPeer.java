/*
 * Copyright (c) 2009, 2013, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

/*
 * This file was originally generated by JSLC
 * and then hand edited for performance.
 */

package com.sun.scenario.effect.impl.sw.sse;

import com.sun.scenario.effect.FilterContext;
import com.sun.scenario.effect.impl.Renderer;

public class SSELinearConvolveShadowPeer extends SSELinearConvolvePeer {
    public SSELinearConvolveShadowPeer(FilterContext fctx, Renderer r, String uniqueName) {
        super(fctx, r, uniqueName);
    }

    private float[] getShadowColor() {
        return getKernel().getShadowColorComponents(getPass());
    }

    private static native void
        filterVector(int dstPixels[], int dstw, int dsth, int dstscan,
                     int srcPixels[], int srcw, int srch, int srcscan,
                     float weights[], int count,
                     float srcx0, float srcy0,
                     float offsetx, float offsety,
                     float deltax, float deltay,
                     float shadowColor[],
                     float dxcol, float dycol, float dxrow, float dyrow);

    @Override
    protected void
        filterVector(int dstPixels[], int dstw, int dsth, int dstscan,
                     int srcPixels[], int srcw, int srch, int srcscan,
                     float weights[], int count,
                     float srcx0, float srcy0,
                     float offsetx, float offsety,
                     float deltax, float deltay,
                     float dxcol, float dycol, float dxrow, float dyrow)
    {
        filterVector(dstPixels, dstw, dsth, dstscan,
                     srcPixels, srcw, srch, srcscan,
                     weights, count,
                     srcx0, srcy0,
                     offsetx, offsety,
                     deltax, deltay, getShadowColor(),
                     dxcol, dycol, dxrow, dyrow);
    }

    /**
     * In the nomenclature of the argument list for this method, "row" refers
     * to the coordinate which increments once for each new stream of single
     * axis data that we are blurring in a single pass.  And "col" refers to
     * the other coordinate that increments along the row.
     * Rows are horizontal in the first pass and vertical in the second pass.
     * Cols are vice versa.
     */
    private static native void
        filterHV(int dstPixels[], int dstcols, int dstrows, int dcolinc, int drowinc,
                 int srcPixels[], int srccols, int srcrows, int scolinc, int srowinc,
                 float weights[], float shadowColor[]);

    @Override
    protected void
        filterHV(int dstPixels[], int dstcols, int dstrows, int dcolinc, int drowinc,
                 int srcPixels[], int srccols, int srcrows, int scolinc, int srowinc,
                 float weights[])
    {
        filterHV(dstPixels, dstcols, dstrows, dcolinc, drowinc,
                 srcPixels, srccols, srcrows, scolinc, srowinc,
                 weights, getShadowColor());
    }
}
