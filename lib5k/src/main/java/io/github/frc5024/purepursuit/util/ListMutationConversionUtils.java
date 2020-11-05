package io.github.frc5024.purepursuit.util;

import java.util.List;

import edu.wpi.first.wpilibj.geometry.Translation2d;
import io.github.frc5024.lib5k.utils.algorithmic.MutableTranslation;

/**
 * Some utilities for converting between mutable and immutable arrays
 */
public class ListMutationConversionUtils {

    /**
     * Convert a list of immutable {@link Translation2d} objects to an array of
     * {@link MutableTranslation} objects
     * 
     * @param immutable Immutable list
     * @return Mutable array
     */
    public static MutableTranslation[] makeMutable(List<Translation2d> immutable) {
        return makeMutable(immutable.toArray(new Translation2d[immutable.size()]));
    }

    /**
     * Convert an array of immutable {@link Translation2d} objects to an array of
     * {@link MutableTranslation} objects
     * 
     * @param immutable Immutable array
     * @return Mutable array
     */
    public static MutableTranslation[] makeMutable(Translation2d[] immutable) {

        // Allocate a mutable array
        MutableTranslation[] mutable = new MutableTranslation[immutable.length];

        // Copy objects over
        for (int i = 0; i < mutable.length; i++) {
            mutable[i] = new MutableTranslation(immutable[i]);
        }

        return mutable;

    }

    /**
     * Convert a list of {@link MutableTranslation} objects to an array of immutable
     * {@link Translation2d} objects
     * 
     * @param mutable Mutable list
     * @return Immutable array
     */
    public static Translation2d[] makeImmutable(List<MutableTranslation> mutable) {
        return makeImmutable(mutable.toArray(new MutableTranslation[mutable.size()]));
    }

    /**
     * Convert an array of {@link MutableTranslation} objects to an array of
     * immutable {@link Translation2d} objects
     * 
     * @param mutable Mutable array
     * @return Immutable array
     */
    public static Translation2d[] makeImmutable(MutableTranslation[] mutable) {

        // Allocate a mutable array
        Translation2d[] immutable = new Translation2d[mutable.length];

        // Copy objects over
        for (int i = 0; i < immutable.length; i++) {
            immutable[i] = mutable[i].getAsTranslation2d();
        }

        return immutable;

    }

}