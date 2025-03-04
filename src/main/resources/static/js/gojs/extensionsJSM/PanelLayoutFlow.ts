﻿/*
*  Copyright (C) 1998-2022 by Northwoods Software Corporation. All Rights Reserved.
*/

/*
* This is an extension and not part of the main GoJS library.
* Note that the API for this class may change with any version, even point releases.
* If you intend to use an extension in production, you should copy the code to your own source directory.
* Extensions can be found in the GoJS kit under the extensions or extensionsTS folders.
* See the Extensions intro page (https://gojs.net/latest/intro/extensions.html) for more information.
*/

import * as go from '../release/go-module.js';

/**
* A custom {@link PanelLayout} that arranges panel elements in rows or columns.
* A typical use might be:
* <pre>
* $(go.Node,
*   ...
*   $(go.Panel, "Flow",
*     ... the elements to be laid out in rows with no space between them ...
*   )
*   ...
* )
* </pre>
* A customized use might be:
* <pre>
* $(go.Node,
*   ...
*   $(go.Panel,
*     $(PanelLayoutFlow, { spacing: new go.Size(5, 5), direction: 90 }),
*     ... the elements to be laid out in columns ...
*   )
*   ...
* )
* </pre>
*
* The {@link #direction} property determines whether the elements are arranged in rows (if 0 or 180)
* or in columns (if 90 or 270).
*
* Use the {@link #spacing} property to control how much space there is between elements in a row or column
* as well as between rows or columns.
*
* This layout respects the {@link GraphObject#visible}, {@link GraphObject#stretch},
* and {@link GraphObject#alignment} properties on each element, along with the Panel's
* {@link Panel#defaultStretch}, {@link Panel#defaultAlignment}, and {@link Panel#padding} properties.
*
* If you want to experiment with this extension, try the <a href="../../extensionsJSM/PanelLayoutFlow.html">Flow PanelLayout</a> sample.
* @category Layout Extension
*/
export class PanelLayoutFlow extends go.PanelLayout {
  private _direction: number = 0;  // only 0 or 180 (rows) or 90 or 270 (columns)
  private _spacing: go.Size = new go.Size(0, 0);  // space between elements and rows/columns
  private _lineBreadths: Array<number> = [];  // row height or column width, excluding spacing
  private _lineLengths: Array<number> = [];  // line length, excluding padding and external spacing

  /**
   * Constructs a PanelLayoutFlow that lays out elements in rows
   * with no space between the elements or between the rows.
   */
  constructor() {
    super();
    this.name = "Flow";
  }

  /**
   * Gets or sets the initial direction in which elements are laid out.
   * The value must be 0 or 180, which results in rows, or 90 or 270, which results in columns.
   *
   * The default value is 0, resulting in rows that go rightward.
   * A value of 90 results in columns that go downward.
   *
   * Setting this property does not notify about any changed event,
   * nor does a change in value automatically cause the panel layout to be performed again.
   */
  get direction() { return this._direction; }
  set direction(d: number) {
    if (d !== 0 && d !== 90 && d !== 180 && d !== 270) throw new Error("bad direction for PanelLayoutFlow: " + d);
    this._direction = d;
  }

  /**
   * Gets or sets the space between adjacent elements in the panel and the space between adjacent rows or columns.
   *
   * The default value is (0, 0).  The size is in the panel's coordinate system.
   *
   * Setting this property does not notify about any changed event,
   * nor does a change in value automatically cause the panel layout to be performed again.
   */
   get spacing() { return this._spacing; }
   set spacing(s: go.Size) { this._spacing = s; }

  public measure(panel: go.Panel, width: number, height: number, elements: Array<go.GraphObject>, union: go.Rect, minw: number, minh: number): void {
    this._lineBreadths = [];
    this._lineLengths = [];
    const pad = <go.Margin>panel.padding;
    const wrapx = width + pad.left;  // might be Infinity
    const wrapy = height + pad.top;
    let x = pad.left;  // account for padding
    let y = pad.top;
    const xstart = x;  // remember start
    const ystart = y;
    // assume that measuring is the same for either direction
    if (this.direction === 0 || this.direction === 180) {
      let maxx = x;  // track total width
      let rowh = 0;  // compute row height
      for (let i = 0; i < elements.length; i++) {
        const elem = elements[i];
        if (!elem.visible) continue;
        this.measureElement(elem, Infinity, height, minw, minh);
        const mb = elem.measuredBounds;
        const marg = <go.Margin>elem.margin;
        const gw = marg.left + mb.width + marg.right;  // gross size including margins
        const gh = marg.top + mb.height + marg.bottom;
        if (x + gw > wrapx && i > 0) {  // next row
          this._lineBreadths.push(rowh);  // remember previous row info
          this._lineLengths.push(x - pad.left);
          y += rowh + this.spacing.height;  // advance x and y
          if (y + gh <= wrapy) {  // next row fits???
            x = xstart + gw + this.spacing.width;
            rowh = gh;
          } else {  // clipped, assume zero size
            x = xstart;
            rowh = 0;
            break;
          }
        } else {  // advance x
          x += gw + this.spacing.width;
          rowh = Math.max(rowh, gh);
        }
        maxx = Math.max(maxx, x);
      }
      this._lineBreadths.push(rowh);
      this._lineLengths.push(x - pad.left);
      union.width = Math.max(0, Math.min(maxx, wrapx) - pad.left);  // don't add padding to union
      union.height = Math.max(0, y + rowh - pad.top);
    } else if (this.direction === 90 || this.direction === 270) {
      let maxy = y;
      let colw = 0;
      for (let i = 0; i < elements.length; i++) {
        const elem = elements[i];
        if (!elem.visible) continue;
        this.measureElement(elem, width, Infinity, minw, minh);
        const mb = elem.measuredBounds;
        const marg = <go.Margin>elem.margin;
        const gw = marg.left + mb.width + marg.right;
        const gh = marg.top + mb.height + marg.bottom;
        if (y + gh > wrapy && i > 0) {
          this._lineBreadths.push(colw);
          this._lineLengths.push(y - pad.top);
          x += colw + this.spacing.width;
          if (x + gw <= wrapx) {
            y = ystart + gh + this.spacing.height;
            colw = gw;
          } else {
            y = ystart;
            colw = 0;
            break;
          }
        } else {
          y += gh + this.spacing.height;
          colw = Math.max(colw, gw);
        }
        maxy = Math.max(maxy, y);
      }
      this._lineBreadths.push(colw);
      this._lineLengths.push(y - pad.top);
      union.width = Math.max(0, x + colw - pad.left);
      union.height = Math.max(0, Math.min(maxy, wrapy) - pad.top);
    }
  }

  private isStretched(horiz: boolean, elt: go.GraphObject, panel: go.Panel): boolean {
    let s = elt.stretch;
    if (s === go.GraphObject.Default) s = panel.defaultStretch;
    if (s === go.GraphObject.Fill) return true;
    return s === (horiz ? go.GraphObject.Vertical : go.GraphObject.Horizontal);
  }

  private align(elt: go.GraphObject, panel: go.Panel): go.Spot {
    let a = elt.alignment;
    if (a.isDefault()) a = panel.defaultAlignment;
    if (!a.isSpot()) a = go.Spot.Center;
    return a;
  }

  public arrange(panel: go.Panel, elements: Array<go.GraphObject>, union: go.Rect): void {
    const pad = <go.Margin>panel.padding;
    let x = (this.direction === 180) ? union.width - pad.right : pad.left;
    let y = (this.direction === 270) ? union.height - pad.bottom : pad.top;
    const xstart = x;
    const ystart = y;
    if (this.direction === 0) {
      let row = 0;
      for (let i = 0; i < elements.length; i++) {
        const elem = elements[i];
        if (!elem.visible) continue;
        const mb = elem.measuredBounds;
        const marg = <go.Margin>elem.margin;
        const gw = marg.left + mb.width + marg.right;
        // use computed row length to decide whether to wrap, account for error accumulation
        if (x - pad.left > this._lineLengths[row] - 0.00000005 && i > 0) {
          y += this._lineBreadths[row++] + this.spacing.height;
          x = xstart;
          if (row === this._lineLengths.length) row--;  // if on last row, stay there
        }
        const lastbr = this._lineBreadths[row];  // if row was clipped,
        let h = (lastbr > 0) ? lastbr - marg.top - marg.bottom : 0;  // use zero height
        let ya = (lastbr > 0) ? y + marg.top : y;  // and stay at same Y point
        if ((lastbr > 0) && !this.isStretched(true, elem, panel)) {  // if aligning...
          const align = this.align(elem, panel);  // compute alignment Spot
          ya += align.y * (h - mb.height) + align.offsetY;
          h = mb.height;  // only considering Y axis
        }
        const xa = x + ((lastbr > 0) ? marg.left : 0);
        this.arrangeElement(elem, xa, ya, mb.width, h);
        x += gw + this.spacing.width;
      }
    } else if (this.direction === 180) {
      let row = 0;
      for (let i = 0; i < elements.length; i++) {
        const elem = elements[i];
        if (!elem.visible) continue;
        const mb = elem.measuredBounds;
        const marg = <go.Margin>elem.margin;
        const gw = marg.left + mb.width + marg.right;
        if (x - gw - pad.left < 0.00000005 && i > 0) {
          y += this._lineBreadths[row++] + this.spacing.height;
          x = xstart;
          if (row === this._lineLengths.length) row--;
        }
        const lastbr = this._lineBreadths[row];
        let h = (lastbr > 0) ? lastbr - marg.top - marg.bottom : 0;
        let ya = (lastbr > 0) ? y + marg.top : y;
        if ((lastbr > 0) && !this.isStretched(true, elem, panel)) {
          const align = this.align(elem, panel);
          ya += align.y * (h - mb.height) + align.offsetY;
          h = mb.height;
        }
        const xa = x - gw + ((lastbr > 0) ? marg.left : 0);
        this.arrangeElement(elem, xa, ya, mb.width, h);
        x -= gw + this.spacing.width;
      }
    } else if (this.direction === 90) {
      let col = 0;
      for (let i = 0; i < elements.length; i++) {
        const elem = elements[i];
        if (!elem.visible) continue;
        const mb = elem.measuredBounds;
        const marg = <go.Margin>elem.margin;
        const gh = marg.top + mb.height + marg.bottom;
        if (y - pad.top > this._lineLengths[col] - 0.00000005 && i > 0) {
          x += this._lineBreadths[col++] + this.spacing.width;
          y = ystart;
          if (col === this._lineLengths.length) col--;
        }
        const lastbr = this._lineBreadths[col];
        let w = (lastbr > 0) ? lastbr - marg.left - marg.right : 0;
        let xa = (lastbr > 0) ? x + marg.left : x;
        if ((lastbr > 0) && !this.isStretched(false, elem, panel)) {
          const align = this.align(elem, panel);
          xa += align.x * (w - mb.width) + align.offsetX;
          w = mb.width;
        }
        const ya = y + ((lastbr > 0) ? marg.top : 0);
        this.arrangeElement(elem, xa, ya, w, mb.height);
        y += gh + this.spacing.height;
      }
    } else if (this.direction === 270) {
      let col = 0;
      for (let i = 0; i < elements.length; i++) {
        const elem = elements[i];
        if (!elem.visible) continue;
        const mb = elem.measuredBounds;
        const marg = <go.Margin>elem.margin;
        const gh = marg.top + mb.height + marg.bottom;
        if (y - gh - pad.top < 0.00000005 && i > 0) {
          x += this._lineBreadths[col++] + this.spacing.width;
          y = ystart - gh;
          if (col === this._lineLengths.length) col--;
        } else {
          y -= gh;
        }
        const lastbr = this._lineBreadths[col];
        let w = (lastbr > 0) ? lastbr - marg.left - marg.right : 0;
        let xa = (lastbr > 0) ? x + marg.left : x;
        if ((lastbr > 0) && !this.isStretched(false, elem, panel)) {
          const align = this.align(elem, panel);
          xa += align.x * (w - mb.width) + align.offsetX;
          w = mb.width;
        }
        const ya = y + ((lastbr > 0) ? marg.top : 0);
        this.arrangeElement(elem, xa, ya, w, mb.height);
        y -= this.spacing.height;
      }
    }
  }

  private static _ = (() => {
    go.Panel.definePanelLayout('Flow', new PanelLayoutFlow());
  })();
}
