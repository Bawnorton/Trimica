package com.bawnorton.trimica.client.colour;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

// https://bottosson.github.io/posts/oklab/
public final class OkLabHelper {
	private static final double[][] M1 = {
			{0.4122214708, 0.5363325363, 0.0514459929},
			{0.2119034982, 0.6806995451, 0.1073969566},
			{0.0883024619, 0.2817188376, 0.6299787005}
	};

	private static final double[][] M2 = {
			{0.2104542553, 0.7936177850, -0.0040720468},
			{1.9779984951, -2.4285922050, 0.4505937099},
			{0.0259040371, 0.7827717662, -0.8086757660}
	};

	private static final double[][] M2_INV = {
			{1.0, 0.3963377774, 0.2158037573},
			{1.0, -0.1055613458, -0.0638541728},
			{1.0, -0.0894841775, -1.2914855480}
	};

	private static final double[][] M1_INV = {
			{4.0767416621, -3.3077115913, 0.2309699292},
			{-1.2684380046, 2.6097574011, -0.3413193965},
			{-0.0041960863, -0.7034186147, 1.7076147010}
	};

	public static double[] rgbToOklab(int rgb) {
		double r = ((rgb >> 16) & 0xFF) / 255.0;
		double g = ((rgb >> 8) & 0xFF) / 255.0;
		double b = (rgb & 0xFF) / 255.0;
		return rgbToOklab(r, g, b);
	}

	public static double[] rgbToOklab(double r, double g, double b) {
		double r_linear = srgbToLinear(r);
		double g_linear = srgbToLinear(g);
		double b_linear = srgbToLinear(b);

		double l = M1[0][0] * r_linear + M1[0][1] * g_linear + M1[0][2] * b_linear;
		double m = M1[1][0] * r_linear + M1[1][1] * g_linear + M1[1][2] * b_linear;
		double s = M1[2][0] * r_linear + M1[2][1] * g_linear + M1[2][2] * b_linear;

		double l_ = Math.cbrt(Math.max(0, l));
		double m_ = Math.cbrt(Math.max(0, m));
		double s_ = Math.cbrt(Math.max(0, s));

		double L = M2[0][0] * l_ + M2[0][1] * m_ + M2[0][2] * s_;
		double a = M2[1][0] * l_ + M2[1][1] * m_ + M2[1][2] * s_;
		double b_ok = M2[2][0] * l_ + M2[2][1] * m_ + M2[2][2] * s_;

		return new double[]{L, a, b_ok};
	}

	public static List<double[]> rgbToOklab(List<Integer> rgbs) {
		List<double[]> oklabList = new ArrayList<>(rgbs.size());
		for (int rgb : rgbs) {
			double[] oklab = rgbToOklab(rgb);
			oklabList.add(oklab);
		}
		return oklabList;
	}

	public static int oklabToRgb(double[] oklab) {
		double L = oklab[0];
		double a = oklab[1];
		double b = oklab[2];

		double[] rgb = oklabToRgb(L, a, b);
		int r = (int) Math.round(rgb[0] * 255);
		int g = (int) Math.round(rgb[1] * 255);
		int b_srgb = (int) Math.round(rgb[2] * 255);
		return (r << 16) | (g << 8) | b_srgb;
	}

	public static double[] oklabToRgb(double L, double a, double b) {
		double l_ = M2_INV[0][0] * L + M2_INV[0][1] * a + M2_INV[0][2] * b;
		double m_ = M2_INV[1][0] * L + M2_INV[1][1] * a + M2_INV[1][2] * b;
		double s_ = M2_INV[2][0] * L + M2_INV[2][1] * a + M2_INV[2][2] * b;

		double l = l_ * l_ * l_;
		double m = m_ * m_ * m_;
		double s = s_ * s_ * s_;

		double r_linear = M1_INV[0][0] * l + M1_INV[0][1] * m + M1_INV[0][2] * s;
		double g_linear = M1_INV[1][0] * l + M1_INV[1][1] * m + M1_INV[1][2] * s;
		double b_linear = M1_INV[2][0] * l + M1_INV[2][1] * m + M1_INV[2][2] * s;

		double r = linearToSrgb(r_linear);
		double g = linearToSrgb(g_linear);
		double b_srgb = linearToSrgb(b_linear);

		r = Math.max(0.0, Math.min(1.0, r));
		g = Math.max(0.0, Math.min(1.0, g));
		b_srgb = Math.max(0.0, Math.min(1.0, b_srgb));

		return new double[]{r, g, b_srgb};
	}

	public static List<Integer> okLabToRgb(List<double[]> okLabColours) {
		List<Integer> rgbList = new ArrayList<>(okLabColours.size());
		for (double[] okLab : okLabColours) {
			int rgb = oklabToRgb(okLab);
			rgbList.add(rgb);
		}
		return rgbList;

	}

	private static double srgbToLinear(double val_srgb) {
		if (val_srgb <= 0.04045) {
			return val_srgb / 12.92;
		} else {
			return Math.pow((val_srgb + 0.055) / 1.055, 2.4);
		}
	}

	private static double linearToSrgb(double val_linear) {
		if (val_linear <= 0.0031308) {
			return 12.92 * val_linear;
		} else {
			return 1.055 * Math.pow(val_linear, 1.0 / 2.4) - 0.055;
		}
	}

	public static @NotNull List<double[]> strechOkLab(int targetSize, int size, List<double[]> oklabPalette) {
		List<double[]> stretchedOKLab = new ArrayList<>(targetSize);
		for (int i = 0; i < targetSize; i++) {
			double t = (double) i / (targetSize - 1);
			int index1 = (int) Math.floor(t * (size - 1));
			int index2 = Math.min(index1 + 1, size - 1);
			double blend = (t * (size - 1)) - index1;

			double[] colour1 = oklabPalette.get(index1);
			double[] colour2 = oklabPalette.get(index2);
			double[] interpolatedColour = interpolateOKLab(colour1, colour2, blend);
			stretchedOKLab.add(interpolatedColour);
		}
		return stretchedOKLab;
	}

	public static double[] interpolateOKLab(double[] colour1, double[] colour2, double blend) {
		double[] result = new double[3];
		for (int i = 0; i < 3; i++) {
			result[i] = colour1[i] * (1 - blend) + colour2[i] * blend;
		}
		return result;
	}

}
