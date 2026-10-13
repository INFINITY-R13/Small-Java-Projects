package com.endless.ui.components;

import java.awt.*;
import java.awt.geom.*;

/**
 * Renders Apple SF Symbol-inspired vector icons with pixel-perfect antialiasing.
 */
public class SFIcon {

    public enum Type {
        PLAY, PAUSE, PREVIOUS, NEXT,
        SHUFFLE, REPEAT, REPEAT_ONE,
        VOLUME_HIGH, VOLUME_MED, VOLUME_LOW, VOLUME_MUTE,
        HEART, HEART_FILLED,
        SEARCH, PLUS, FOLDER, SUN, MOON,
        WAVEFORM, EQUALIZER, CLOSE, MINIMIZE, MAXIMIZE,
        INFINITY, QUEUE, CHECK
    }

    public static void draw(Graphics2D g2, Type type, float x, float y, float size, Color color) {
        Graphics2D g = (Graphics2D) g2.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        g.setColor(color);
        g.translate(x, y);

        float s = size;
        float sw = Math.max(1.5f, s * 0.08f);
        g.setStroke(new BasicStroke(sw, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        switch (type) {
            case PLAY: {
                // Apple-rounded play triangle
                Path2D path = new Path2D.Float();
                float left = s * 0.28f;
                float right = s * 0.78f;
                float top = s * 0.20f;
                float bottom = s * 0.80f;
                float midY = s * 0.50f;

                path.moveTo(left, top);
                path.lineTo(right, midY);
                path.lineTo(left, bottom);
                path.closePath();
                g.fill(path);
                break;
            }
            case PAUSE: {
                // Two rounded vertical bars
                float barW = s * 0.16f;
                float barH = s * 0.56f;
                float top = s * 0.22f;
                float b1X = s * 0.26f;
                float b2X = s * 0.58f;
                g.fill(new RoundRectangle2D.Float(b1X, top, barW, barH, barW, barW));
                g.fill(new RoundRectangle2D.Float(b2X, top, barW, barH, barW, barW));
                break;
            }
            case PREVIOUS: {
                // Bar + triangle pointing left
                float barX = s * 0.20f;
                float barW = s * 0.10f;
                float barH = s * 0.52f;
                float top = s * 0.24f;
                g.fill(new RoundRectangle2D.Float(barX, top, barW, barH, barW, barW));

                Path2D path = new Path2D.Float();
                path.moveTo(s * 0.80f, top);
                path.lineTo(s * 0.35f, s * 0.50f);
                path.lineTo(s * 0.80f, s * 0.76f);
                path.closePath();
                g.fill(path);
                break;
            }
            case NEXT: {
                // Triangle pointing right + bar
                Path2D path = new Path2D.Float();
                float top = s * 0.24f;
                path.moveTo(s * 0.20f, top);
                path.lineTo(s * 0.65f, s * 0.50f);
                path.lineTo(s * 0.20f, s * 0.76f);
                path.closePath();
                g.fill(path);

                float barX = s * 0.70f;
                float barW = s * 0.10f;
                float barH = s * 0.52f;
                g.fill(new RoundRectangle2D.Float(barX, top, barW, barH, barW, barW));
                break;
            }
            case SHUFFLE: {
                // Intersecting curved arrows
                Path2D p1 = new Path2D.Float();
                p1.moveTo(s * 0.20f, s * 0.32f);
                p1.curveTo(s * 0.45f, s * 0.32f, s * 0.55f, s * 0.68f, s * 0.80f, s * 0.68f);
                g.draw(p1);

                // Arrow head 1
                Path2D a1 = new Path2D.Float();
                a1.moveTo(s * 0.70f, s * 0.58f);
                a1.lineTo(s * 0.82f, s * 0.68f);
                a1.lineTo(s * 0.70f, s * 0.78f);
                g.draw(a1);

                Path2D p2 = new Path2D.Float();
                p2.moveTo(s * 0.20f, s * 0.68f);
                p2.curveTo(s * 0.38f, s * 0.68f, s * 0.46f, s * 0.55f, s * 0.50f, s * 0.50f);
                g.draw(p2);

                Path2D p3 = new Path2D.Float();
                p3.moveTo(s * 0.54f, s * 0.45f);
                p3.curveTo(s * 0.58f, s * 0.40f, s * 0.65f, s * 0.32f, s * 0.80f, s * 0.32f);
                g.draw(p3);

                // Arrow head 2
                Path2D a2 = new Path2D.Float();
                a2.moveTo(s * 0.70f, s * 0.22f);
                a2.lineTo(s * 0.82f, s * 0.32f);
                a2.lineTo(s * 0.70f, s * 0.42f);
                g.draw(a2);
                break;
            }
            case REPEAT:
            case REPEAT_ONE: {
                // Looping circle arrows
                float r = s * 0.26f;
                float cx = s * 0.50f;
                float cy = s * 0.50f;
                Arc2D.Float arc = new Arc2D.Float(cx - r, cy - r, r * 2, r * 2, 40, 280, Arc2D.OPEN);
                g.draw(arc);

                // Arrow head at top right
                Path2D ah = new Path2D.Float();
                ah.moveTo(cx + r * 0.5f, cy - r * 1.25f);
                ah.lineTo(cx + r * 1.0f, cy - r * 0.8f);
                ah.lineTo(cx + r * 0.6f, cy - r * 0.4f);
                g.draw(ah);

                if (type == Type.REPEAT_ONE) {
                    // Small "1" in the center
                    Font font = new Font(Font.SANS_SERIF, Font.BOLD, (int) (s * 0.32f));
                    g.setFont(font);
                    g.drawString("1", s * 0.42f, s * 0.60f);
                }
                break;
            }
            case VOLUME_HIGH:
            case VOLUME_MED:
            case VOLUME_LOW:
            case VOLUME_MUTE: {
                // Speaker horn
                Path2D horn = new Path2D.Float();
                horn.moveTo(s * 0.18f, s * 0.38f);
                horn.lineTo(s * 0.32f, s * 0.38f);
                horn.lineTo(s * 0.50f, s * 0.22f);
                horn.lineTo(s * 0.50f, s * 0.78f);
                horn.lineTo(s * 0.32f, s * 0.62f);
                horn.lineTo(s * 0.18f, s * 0.62f);
                horn.closePath();
                g.fill(horn);

                if (type == Type.VOLUME_MUTE) {
                    // Cross slash
                    g.draw(new Line2D.Float(s * 0.62f, s * 0.38f, s * 0.82f, s * 0.62f));
                    g.draw(new Line2D.Float(s * 0.82f, s * 0.38f, s * 0.62f, s * 0.62f));
                } else {
                    // Curved sound waves
                    g.draw(new Arc2D.Float(s * 0.38f, s * 0.34f, s * 0.32f, s * 0.32f, -50, 100, Arc2D.OPEN));
                    if (type == Type.VOLUME_MED || type == Type.VOLUME_HIGH) {
                        g.draw(new Arc2D.Float(s * 0.32f, s * 0.24f, s * 0.52f, s * 0.52f, -50, 100, Arc2D.OPEN));
                    }
                    if (type == Type.VOLUME_HIGH) {
                        g.draw(new Arc2D.Float(s * 0.26f, s * 0.14f, s * 0.72f, s * 0.72f, -50, 100, Arc2D.OPEN));
                    }
                }
                break;
            }
            case HEART:
            case HEART_FILLED: {
                // Smooth Apple heart
                Path2D heart = new Path2D.Float();
                float cx = s * 0.50f;
                float cy = s * 0.35f;
                heart.moveTo(cx, s * 0.80f);
                heart.curveTo(s * 0.18f, s * 0.58f, s * 0.12f, s * 0.32f, s * 0.30f, s * 0.22f);
                heart.curveTo(s * 0.42f, s * 0.15f, cx, s * 0.30f, cx, s * 0.36f);
                heart.curveTo(cx, s * 0.30f, s * 0.58f, s * 0.15f, s * 0.70f, s * 0.22f);
                heart.curveTo(s * 0.88f, s * 0.32f, s * 0.82f, s * 0.58f, cx, s * 0.80f);
                heart.closePath();
                if (type == Type.HEART_FILLED) {
                    g.fill(heart);
                } else {
                    g.draw(heart);
                }
                break;
            }
            case SEARCH: {
                // Magnifying glass
                float r = s * 0.24f;
                float cx = s * 0.42f;
                float cy = s * 0.42f;
                g.draw(new Ellipse2D.Float(cx - r, cy - r, r * 2, r * 2));
                float handleX = cx + (float) (r * Math.cos(Math.PI / 4));
                float handleY = cy + (float) (r * Math.sin(Math.PI / 4));
                g.draw(new Line2D.Float(handleX, handleY, s * 0.80f, s * 0.80f));
                break;
            }
            case PLUS: {
                g.draw(new Line2D.Float(s * 0.50f, s * 0.22f, s * 0.50f, s * 0.78f));
                g.draw(new Line2D.Float(s * 0.22f, s * 0.50f, s * 0.78f, s * 0.50f));
                break;
            }
            case FOLDER: {
                Path2D f = new Path2D.Float();
                f.moveTo(s * 0.18f, s * 0.30f);
                f.lineTo(s * 0.42f, s * 0.30f);
                f.lineTo(s * 0.50f, s * 0.38f);
                f.lineTo(s * 0.82f, s * 0.38f);
                f.lineTo(s * 0.82f, s * 0.74f);
                f.lineTo(s * 0.18f, s * 0.74f);
                f.closePath();
                g.draw(f);
                break;
            }
            case SUN: {
                float cr = s * 0.18f;
                g.draw(new Ellipse2D.Float(s * 0.5f - cr, s * 0.5f - cr, cr * 2, cr * 2));
                for (int i = 0; i < 8; i++) {
                    double angle = i * Math.PI / 4.0;
                    float x1 = s * 0.5f + (float) Math.cos(angle) * s * 0.28f;
                    float y1 = s * 0.5f + (float) Math.sin(angle) * s * 0.28f;
                    float x2 = s * 0.5f + (float) Math.cos(angle) * s * 0.38f;
                    float y2 = s * 0.5f + (float) Math.sin(angle) * s * 0.38f;
                    g.draw(new Line2D.Float(x1, y1, x2, y2));
                }
                break;
            }
            case MOON: {
                Area moon = new Area(new Ellipse2D.Float(s * 0.22f, s * 0.22f, s * 0.56f, s * 0.56f));
                Area sub = new Area(new Ellipse2D.Float(s * 0.36f, s * 0.18f, s * 0.52f, s * 0.52f));
                moon.subtract(sub);
                g.fill(moon);
                break;
            }
            case WAVEFORM: {
                float[] heights = {0.3f, 0.6f, 0.9f, 0.45f, 0.75f, 0.35f};
                float barW = s * 0.08f;
                for (int i = 0; i < heights.length; i++) {
                    float bx = s * 0.18f + i * (s * 0.12f);
                    float bh = s * 0.55f * heights[i];
                    float by = s * 0.5f - bh / 2f;
                    g.fill(new RoundRectangle2D.Float(bx, by, barW, bh, barW, barW));
                }
                break;
            }
            case EQUALIZER: {
                // Mini 3-bar animated icon
                float bw = s * 0.14f;
                g.fill(new RoundRectangle2D.Float(s * 0.18f, s * 0.25f, bw, s * 0.55f, bw, bw));
                g.fill(new RoundRectangle2D.Float(s * 0.43f, s * 0.15f, bw, s * 0.70f, bw, bw));
                g.fill(new RoundRectangle2D.Float(s * 0.68f, s * 0.38f, bw, s * 0.42f, bw, bw));
                break;
            }
            case INFINITY: {
                // Endless logo mark: smooth lemniscate infinity
                Path2D inf = new Path2D.Float();
                float midX = s * 0.50f;
                float midY = s * 0.50f;
                float w = s * 0.30f;
                float h = s * 0.20f;
                inf.moveTo(midX, midY);
                inf.curveTo(midX + w * 0.6f, midY - h, midX + w, midY - h, midX + w, midY);
                inf.curveTo(midX + w, midY + h, midX + w * 0.6f, midY + h, midX, midY);
                inf.curveTo(midX - w * 0.6f, midY - h, midX - w, midY - h, midX - w, midY);
                inf.curveTo(midX - w, midY + h, midX - w * 0.6f, midY + h, midX, midY);
                g.setStroke(new BasicStroke(sw * 1.3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g.draw(inf);
                break;
            }
            case QUEUE: {
                float lw = s * 0.60f;
                float lx = s * 0.20f;
                g.draw(new Line2D.Float(lx, s * 0.30f, lx + lw, s * 0.30f));
                g.draw(new Line2D.Float(lx, s * 0.50f, lx + lw, s * 0.50f));
                g.draw(new Line2D.Float(lx, s * 0.70f, lx + lw * 0.6f, s * 0.70f));
                break;
            }
            case CHECK: {
                Path2D chk = new Path2D.Float();
                chk.moveTo(s * 0.22f, s * 0.52f);
                chk.lineTo(s * 0.42f, s * 0.72f);
                chk.lineTo(s * 0.78f, s * 0.28f);
                g.draw(chk);
                break;
            }
        }

        g.dispose();
    }
}
