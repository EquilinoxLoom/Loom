/**
 * This module contains classes designed for duck typing enums.
 * <p>
 * Duck typing, a programming concept emphasizing object behavior over explicit types or classes, is applied here to
 * extend the behavior of existing enums in the game code. In Java, an object's type is determined by its methods and
 * properties rather than its inheritance hierarchy or explicit interface implementation.
 * </p>
 * <p>
 * Enums in Java are immutable classes, making it challenging to add constants dynamically. To address this limitation,
 * duck-typed classes are introduced into the code using {@link org.spongepowered.asm.mixin.injection.Inject mixin}.
 * </p>
 *
 * @since 0.1
 * @author sandáliaball
 * @version 0.1
 */
package loom.equilinox.ducktype;