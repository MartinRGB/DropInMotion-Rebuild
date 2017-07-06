/*
 * $Id$
 *
 * Copyright 2005 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */

package org.progx.dropinmotion.shadow;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ConvolveOp;
import java.awt.image.DataBufferInt;
import java.awt.image.Kernel;
import java.awt.image.WritableRaster;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.HashMap;

/**
 * <p>A shadow factory generates a drop shadow for any given picture, respecting
 * the transparency channel if present. The resulting picture contains the
 * shadow only and to create a drop shadow effect you will need to stack the
 * original picture and the shadow generated by the factory. If you are using
 * Swing you can get this done very easily with the layout
 * {@link org.jdesktop.swingx.StackLayout}.</p>
 * <h2>Shadow Properties</h2>
 * <p>A shadow is defined by three properties:
 * <ul>
 *   <li><i>size</i>: The size, in pixels, of the shadow. This property also
 *   defines the fuzzyness.</li>
 *   <li><i>opacity</i>: The opacity, between 0.0 and 1.0, of the shadow.</li>
 *   <li><i>color</i>: The color of the shadow. Shadows are not meant to be
 *   black only.</li>
 * </ul>
 * You can set these properties using the provided mutaters or the appropriate
 * constructor. Here are two ways of creating a green shadow of size 10 and
 * with an opacity of 50%:
 * <pre>
 * ShadowFactory factory = new ShadowFactory(10, 0.5f, Color.GREEN);
 * // ..
 * factory = new ShadowFactory();
 * factory.setSize(10);
 * factory.setOpacity(0.5f);
 * factory.setColor(Color.GREEN);
 * </pre>
 * The default constructor provides the following default values:
 * <ul>
 *   <li><i>size</i>: 5 pixels</li>
 *   <li><i>opacity</i>: 50%</li>
 *   <li><i>color</i>: Black</li>
 * </ul></p>
 * <h2>Shadow Quality</h2>
 * <p>The factory provides two shadow generation algorithms: <i>fast quality blur</i>
 * and <i>high quality blur</i>. You can select your preferred algorithm by
 * setting the appropriate rendering hint:
 * <pre>
 * ShadowFactory factory = new ShadowFactory();
 * factory.setRenderingHint(ShadowFactory.KEY_BLUR_QUALITY,
 *                          ShadowFactory.VALUE_BLUR_QUALITY_HIGH);
 * </pre>
 * The default rendering algorihtm is <code>VALUE_BLUR_QUALITY_FAST</code>.</p>
 * <p>The current implementation should provide the same quality with both
 * algorithms but performances are guaranteed to be better (about 30 times
 * faster) with the <i>fast quality blur</i>.</p>
 * <h2>Generating a Shadow</h2>
 * <p>A shadow is generated as a <code>BufferedImage</code> from another
 * <code>BufferedImage</code>. Once the factory is set up, you must call
 * {@link #createShadow} to actually generate the shadow:
 * <pre>
 * ShadowFactory factory = new ShadowFactory();
 * // factory setup
 * BufferedImage shadow = factory.createShadow(bufferedImage); 
 * </pre>
 * The resulting image is of type <code>BufferedImage.TYPE_INT_ARGB</code>.
 * Both dimensions of this image are larger than original image's:
 * <ul>
 *   <li>new width = original width + 2 * shadow size</li>
 *   <li>new height = original height + 2 * shadow size</li>
 * </ul>
 * This must be taken into account when you need to create a drop shadow effect.</p>
 * <h2>Properties Changes</h2>
 * <p>This factory allows to register property change listeners with
 * {@link #addPropertyChangeListener}. Listening to properties changes is very
 * useful when you emebed the factory in a graphical component and give the API
 * user the ability to access the factory. By listening to properties changes,
 * you can easily repaint the component when needed.</p>
 * <h2>Threading Issues</h2>
 * <p><code>ShadowFactory</code> is not guaranteed to be thread-safe.</p>
 * 
 * @author Romain Guy <romain.guy@mac.com>
 * @author S�bastien Petrucci <sebastien_petrucci@yahoo.fr>
 */

public class ShadowFactory {
    /**
     * <p>Key for the blur quality rendering hint.</p>
     */
    public static final String KEY_BLUR_QUALITY = "blur_quality";

    /**
     * <p>Selects the fast rendering algorithm. This is the default rendering
     * hint for <code>KEY_BLUR_QUALITY</code>.</p>
     */
    public static final String VALUE_BLUR_QUALITY_FAST = "fast";
    
    /**
     * <p>Selects the high quality rendering algorithm. With current implementation,
     * This algorithm does not guarantee a better rendering quality and should
     * not be used.</p>
     */
    public static final String VALUE_BLUR_QUALITY_HIGH = "high";

    /**
     * <p>Identifies a change to the size used to render the shadow.</p>
     * <p>When the property change event is fired, the old value and the new
     * value are provided as <code>Integer</code> instances.</p>
     */
    public static final String SIZE_CHANGED_PROPERTY = "shadow_size";
    
    /**
     * <p>Identifies a change to the opacity used to render the shadow.</p>
     * <p>When the property change event is fired, the old value and the new
     * value are provided as <code>Float</code> instances.</p>
     */
    public static final String OPACITY_CHANGED_PROPERTY = "shadow_opacity";
    
    /**
     * <p>Identifies a change to the color used to render the shadow.</p>
     */
    public static final String COLOR_CHANGED_PROPERTY = "shadow_color";

    // size of the shadow in pixels (defines the fuzziness)
    private int size = 5;
    
    // opacity of the shadow
    private float opacity = 0.5f;
    
    // color of the shadow
    private Color color = Color.BLACK;

    // rendering hints map
    private HashMap<Object, Object> hints;
    
    // notifies listeners of properties changes
    private PropertyChangeSupport changeSupport;

    /**
     * <p>Creates a default good looking shadow generator.
     * The default shadow factory provides the following default values:
     * <ul>
     *   <li><i>size</i>: 5 pixels</li>
     *   <li><i>opacity</i>: 50%</li>
     *   <li><i>color</i>: Black</li>
     *   <li><i>rendering quality</i>: VALUE_BLUR_QUALITY_FAST</li>
     * </ul></p>
     * <p>These properties provide a regular, good looking shadow.</p>
     */
    public ShadowFactory() {
        this(5, 0.5f, Color.BLACK);
    }
    
    /**
     * <p>A shadow factory needs three properties to generate shadows.
     * These properties are:</p> 
     * <ul>
     *   <li><i>size</i>: The size, in pixels, of the shadow. This property also
     *   defines the fuzzyness.</li>
     *   <li><i>opacity</i>: The opacity, between 0.0 and 1.0, of the shadow.</li>
     *   <li><i>color</i>: The color of the shadow. Shadows are not meant to be
     *   black only.</li>
     * </ul></p>
     * <p>Besides these properties you can set rendering hints to control the
     * rendering process. The default rendering hints let the factory use the
     * fastest shadow generation algorithm.</p>
     * @param size The size of the shadow in pixels. Defines the fuzziness.
     * @param opacity The opacity of the shadow.
     * @param color The color of the shadow.
     * @see #setRenderingHint(Object, Object)
     */
    public ShadowFactory(final int size, final float opacity, final Color color) {
        hints = new HashMap<Object, Object>();
        hints.put(KEY_BLUR_QUALITY, VALUE_BLUR_QUALITY_FAST);
        
        changeSupport = new PropertyChangeSupport(this);

        setSize(size);
        setOpacity(opacity);
        setColor(color);
    }

    /**
     * <p>Add a PropertyChangeListener to the listener list. The listener is
     * registered for all properties. The same listener object may be added
     * more than once, and will be called as many times as it is added. If
     * <code>listener</code> is null, no exception is thrown and no action
     * is taken.</p> 
     * @param listener the PropertyChangeListener to be added
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.addPropertyChangeListener(listener);
    }

    /**
     * <p>Remove a PropertyChangeListener from the listener list. This removes
     * a PropertyChangeListener that was registered for all properties. If
     * <code>listener</code> was added more than once to the same event source,
     * it will be notified one less time after being removed. If
     * <code>listener</code> is null, or was never added, no exception is thrown
     * and no action is taken.</p>
     * @param listener
     */
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.removePropertyChangeListener(listener);
    }

    /**
     * <p>Maps the specified rendering hint <code>key</code> to the specified
     * <code>value</code> in this <code>SahdowFactory</code> object.</p>
     * @param key The rendering hint key
     * @param value The rendering hint value
     */
    public void setRenderingHint(final Object key, final Object value) {
        hints.put(key, value);
    }

    /**
     * <p>Gets the color used by the factory to generate shadows.</p>
     * @return this factory's shadow color
     */
    public Color getColor() {
        return color;
    }

    /**
     * <p>Sets the color used by the factory to generate shadows.</p>
     * <p>Consecutive calls to {@link #createShadow} will all use this color
     * until it is set again.</p>
     * <p>If the color provided is null, the previous color will be retained.</p>
     * @param shadowColor the generated shadows color
     */
    public void setColor(final Color shadowColor) {
        if (shadowColor != null) {
            Color oldColor = this.color;
            this.color = shadowColor;
            changeSupport.firePropertyChange(COLOR_CHANGED_PROPERTY,
                                             oldColor,
                                             this.color);
        }
    }

    /**
     * <p>Gets the opacity used by the factory to generate shadows.</p>
     * <p>The opacity is comprised between 0.0f and 1.0f; 0.0f being fully
     * transparent and 1.0f fully opaque.</p>
     * @return this factory's shadow opacity
     */
    public float getOpacity() {
        return opacity;
    }

    /**
     * <p>Sets the opacity used by the factory to generate shadows.</p>
     * <p>Consecutive calls to {@link #createShadow} will all use this color
     * until it is set again.</p>
     * <p>The opacity is comprised between 0.0f and 1.0f; 0.0f being fully
     * transparent and 1.0f fully opaque. If you provide a value out of these
     * boundaries, it will be restrained to the closest boundary.</p>
     * @param shadowOpacity the generated shadows opacity
     */
    public void setOpacity(final float shadowOpacity) {
        float oldOpacity = this.opacity;
        
        if (shadowOpacity < 0.0) {
            this.opacity = 0.0f;
        } else if (shadowOpacity > 1.0f) {
            this.opacity = 1.0f;
        } else {
            this.opacity = shadowOpacity;
        }
        
        changeSupport.firePropertyChange(OPACITY_CHANGED_PROPERTY,
                                         new Float(oldOpacity),
                                         new Float(this.opacity));
    }

    /**
     * <p>Gets the size in pixel used by the factory to generate shadows.</p>
     * @return this factory's shadow size
     */
    public int getSize() {
        return size;
    }

    /**
     * <p>Sets the size, in pixels, used by the factory to generate shadows.</p>
     * <p>The size defines the blur radius applied to the shadow to create the
     * fuzziness.</p>
     * <p>There is virtually no limit to the size but it has an impact on shadow
     * generation performances. The greater this value, the longer it will take 
     * to generate the shadow. Remember the generated shadow image dimensions 
     * are computed as follow:
     * <ul>
     *   <li>new width = original width + 2 * shadow size</li>
     *   <li>new height = original height + 2 * shadow size</li>
     * </ul>
     * The size cannot be negative. If you provide a negative value, the size
     * will be 0 instead.</p>
     * @param shadowSize the generated shadows size in pixels (fuzziness)
     */
    public void setSize(final int shadowSize) {
        int oldSize = this.size;
        
        if (shadowSize < 0) {
            this.size = 0;
        } else {
            this.size = shadowSize;
        }
        
        changeSupport.firePropertyChange(SIZE_CHANGED_PROPERTY,
                                         new Integer(oldSize),
                                         new Integer(this.size));
    }

    /**
     * <p>Generates the shadow for a given picture and the current properties
     * of the factory.</p>
     * <p>The generated shadow image dimensions are computed as follow:
     *  <ul>
     *  <li>new width = original width + 2 * shadow size</li>
     *  <li>new height = original height + 2 * shadow size</li>
     * </ul></p>
     * <p>The time taken by a call to this method depends on the size of the
     * shadow, the larger the longer it takes, and on the selected rendering
     * algorithm.</p>
     * @param image the picture from which the shadow must be cast
     * @return the picture containing the shadow of <code>image</code> 
     */
    public BufferedImage createShadow(final BufferedImage image) {
        if (hints.get(KEY_BLUR_QUALITY) == VALUE_BLUR_QUALITY_HIGH) {
            // the high quality algorithm is a 3-pass algorithm
            // it goes through all the pixels of the original picture at least
            // three times to generate the shadow
            // it is easy to understand but very slow
            BufferedImage subject = prepareImage(image);
            BufferedImage shadow = new BufferedImage(subject.getWidth(),
                                                     subject.getHeight(),
                                                     BufferedImage.TYPE_INT_ARGB);
            BufferedImage shadowMask = createShadowMask(subject);
            getLinearBlurOp(size).filter(shadowMask, shadow);
            return shadow;
        }

        // call the fast rendering algorithm
        return createShadowFast(image);
    }
    
    // prepares the picture for the high quality rendering algorithm
    private BufferedImage prepareImage(final BufferedImage image) {
        BufferedImage subject = new BufferedImage(image.getWidth() + size * 2,
                                                  image.getHeight() + size * 2,
                                                  BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2 = subject.createGraphics();
        g2.drawImage(image, null, size, size);
        g2.dispose();

        return subject;
    }

    // fast rendering algorithm
    // basically applies duplicates the picture and applies a size*size kernel
    // in only one pass.
    // the kernel is simulated by an horizontal and a vertical pass
    // implemented by S�bastien Petrucci
    private BufferedImage createShadowFast(final BufferedImage src) {
        int shadowSize = this.size;

        int srcWidth = src.getWidth();
        int srcHeight = src.getHeight();

        int dstWidth = srcWidth + size;
        int dstHeight = srcHeight + size;

        int left = (shadowSize - 1) >> 1;
        int right = shadowSize - left;

        int yStop = dstHeight - right;

        BufferedImage dst = new BufferedImage(dstWidth, dstHeight,
                                              BufferedImage.TYPE_INT_ARGB);

        int shadowRgb = color.getRGB() & 0x00FFFFFF;

        int[] aHistory = new int[shadowSize];
        int historyIdx;

        int aSum;

        ColorModel srcColorModel = src.getColorModel();
        WritableRaster srcRaster = src.getRaster();
        int[] dstBuffer = ((DataBufferInt) dst.getRaster().getDataBuffer()).getData();

        int lastPixelOffset = right * dstWidth;
        float hSumDivider = 1.0f / size;
        float vSumDivider = opacity / size;

        // horizontal pass : extract the alpha mask from the source picture and
        // blur it into the destination picture
        for (int srcY = 0, dstOffset = left * dstWidth; srcY < srcHeight; srcY++) {

            // first pixels are empty
            for (historyIdx = 0; historyIdx < shadowSize; ) {
                aHistory[historyIdx++] = 0;
            }

            aSum = 0;
            historyIdx = 0;

            // compute the blur average with pixels from the source image
            for (int srcX = 0; srcX < srcWidth; srcX++) {

                int a = (int) (aSum * hSumDivider); // calculate alpha value
                dstBuffer[dstOffset++] = a << 24;   // store the alpha value only
                                                    // the shadow color will be added in the next pass

                aSum -= aHistory[historyIdx]; // substract the oldest pixel from the sum

                // extract the new pixel ...
                a = srcColorModel.getAlpha(srcRaster.getDataElements(srcX, srcY, null));
                aHistory[historyIdx] = a;   // ... and store its value into history
                aSum += a;                  // ... and add its value to the sum

                if (++historyIdx >= shadowSize) {
                    historyIdx -= shadowSize;
                }
            }

            // blur the end of the row - no new pixels to grab
            for (int i = 0; i < shadowSize; i++) {

                int a = (int) (aSum * hSumDivider);
                dstBuffer[dstOffset++] = a << 24;

                // substract the oldest pixel from the sum ... and nothing new to add !
                aSum -= aHistory[historyIdx];

                if (++historyIdx >= shadowSize) {
                    historyIdx -= shadowSize;
                }
            }
        }

        // vertical pass
        for (int x = 0, bufferOffset = 0; x < dstWidth; x++, bufferOffset = x) {

            aSum = 0;

            // first pixels are empty
            for (historyIdx = 0; historyIdx < left;) {
                aHistory[historyIdx++] = 0;
            }

            // and then they come from the dstBuffer
            for (int y = 0; y < right; y++, bufferOffset += dstWidth) {
                int a = dstBuffer[bufferOffset] >>> 24;         // extract alpha
                aHistory[historyIdx++] = a;                     // store into history
                aSum += a;                                      // and add to sum
            }

            bufferOffset = x;
            historyIdx = 0;

            // compute the blur average with pixels from the previous pass
            for (int y = 0; y < yStop; y++, bufferOffset += dstWidth) {

                int a = (int) (aSum * vSumDivider);             // calculate alpha value
                dstBuffer[bufferOffset] = a << 24 | shadowRgb;  // store alpha value + shadow color

                aSum -= aHistory[historyIdx];   // substract the oldest pixel from the sum

                a = dstBuffer[bufferOffset + lastPixelOffset] >>> 24;   // extract the new pixel ...
                aHistory[historyIdx] = a;                               // ... and store its value into history
                aSum += a;                                              // ... and add its value to the sum

                if (++historyIdx >= shadowSize) {
                    historyIdx -= shadowSize;
                }
            }

            // blur the end of the column - no pixels to grab anymore
            for (int y = yStop; y < dstHeight; y++, bufferOffset += dstWidth) {

                int a = (int) (aSum * vSumDivider);
                dstBuffer[bufferOffset] = a << 24 | shadowRgb;

                aSum -= aHistory[historyIdx];   // substract the oldest pixel from the sum

                if (++historyIdx >= shadowSize) {
                    historyIdx -= shadowSize;
                }
            }
        }

        return dst;
    }

    // creates the shadow mask for the original picture
    // it colorize all the pixels with the shadow color according to their
    // original transparency
    private BufferedImage createShadowMask(final BufferedImage image) {
        BufferedImage mask = new BufferedImage(image.getWidth(),
                                               image.getHeight(),
                                               BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2d = mask.createGraphics();
        g2d.drawImage(image, 0, 0, null);
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_IN,
                                                    opacity));
        g2d.setColor(color);
        g2d.fillRect(0, 0, image.getWidth(), image.getHeight());
        g2d.dispose();

        return mask;
    }

    // creates a blur convolve operation by generating a kernel of
    // dimensions (size, size).
    private ConvolveOp getLinearBlurOp(final int size) {
        float[] data = new float[size * size];
        float value = 1.0f / (float) (size * size);
        for (int i = 0; i < data.length; i++) {
            data[i] = value;
        }
        return new ConvolveOp(new Kernel(size, size, data));
    }
}