package com.androidfuture.frames.service;

import java.util.ArrayList;

import com.androidfuture.frames.R;
import com.androidfuture.frames.data.FilterInfo;
import com.androidfuture.imagefilter.BlackWhiteFilter;
import com.androidfuture.imagefilter.BrightContrastFilter;
import com.androidfuture.imagefilter.FilmFilter;
import com.androidfuture.imagefilter.Gradient;
import com.androidfuture.imagefilter.HslModifyFilter;
import com.androidfuture.imagefilter.IImageFilter;
import com.androidfuture.imagefilter.LightFilter;
import com.androidfuture.imagefilter.LomoFilter;
import com.androidfuture.imagefilter.NightVisionFilter;
import com.androidfuture.imagefilter.OilPaintFilter;
import com.androidfuture.imagefilter.OldPhotoFilter;
import com.androidfuture.imagefilter.PosterizeFilter;
import com.androidfuture.imagefilter.RainBowFilter;
import com.androidfuture.imagefilter.SaturationModifyFilter;
import com.androidfuture.imagefilter.SceneFilter;
import com.androidfuture.imagefilter.SepiaFilter;
import com.androidfuture.imagefilter.ThreeDGridFilter;
import com.androidfuture.imagefilter.VintageFilter;

public class FilterManager {
	static FilterManager instance;
	static public FilterManager getInstance()
	{
		if(instance == null)
		{
			instance = new FilterManager();
		}
		return instance;
	}
	static ArrayList<FilterInfo> filterArray;
	
	static public ArrayList<FilterInfo> getFilterList()
	{
		if(filterArray == null)
		{
			init();
		}
		return filterArray;
	}
	
	public static IImageFilter getFilter(int position)
	{
		if(filterArray == null)
			init();
		if (filterArray.size() <= position)
			return null;
		else 
			return filterArray.get(position).filter;
	}
	private static void init()
	{
		filterArray = new ArrayList<FilterInfo>();
		filterArray.add(new FilterInfo(R.drawable.filter_orig,null,"orig"));
		filterArray.add(new FilterInfo(R.drawable.filter_lomo, (IImageFilter) new LomoFilter(), "Lomo"));
		filterArray.add(new FilterInfo(R.drawable.filter_rainbow,new RainBowFilter(), "Rainbow"));
		filterArray.add(new FilterInfo(R.drawable.filter_film, new FilmFilter(80f), "Film"));
		//filterArray.add(new FilterInfo(R.drawable.filter_vantage,new VintageFilter(),"Vintage"));
		filterArray.add(new FilterInfo(R.drawable.filter_bright,new BrightContrastFilter(),"Bright"));
		filterArray.add(new FilterInfo(R.drawable.filter_saturation,	new SaturationModifyFilter(),"Saturation"));
		filterArray.add(new FilterInfo(R.drawable.filter_light,	new LightFilter(), "Light"));
		filterArray.add(new FilterInfo(R.drawable.filter_sepia,	new SepiaFilter(), "Sepia"));


		//filterArray.add(new FilterInfo(R.drawable.filter_night,new NightVisionFilter(), "Night"));	
		//filterArray.add(new FilterInfo(R.drawable.filter_grid, new ThreeDGridFilter(16, 100), "Grid"));
		filterArray.add(new FilterInfo(R.drawable.filter_hsl100, new HslModifyFilter(100f), "HSL100"));
		filterArray.add(new FilterInfo(R.drawable.filter_hsl300, new HslModifyFilter(300f),"HSL300"));
		filterArray.add(new FilterInfo(R.drawable.filter_gray, new BlackWhiteFilter(), "Gray"));
		//filterArray.add(new FilterInfo(R.drawable.filter_poster, new PosterizeFilter(2),"Posterize"));
		filterArray.add(new FilterInfo(R.drawable.filter_green, new SceneFilter(5f, Gradient.Scene()), "Green"));//green
		//filterArray.add(new FilterInfo(R.drawable.filter_purple, new SceneFilter(5f, Gradient.Scene1()), "Purple"));//purple
		//filterArray.add(new FilterInfo(R.drawable.filter_blue, new SceneFilter(5f, Gradient.Scene2()), "Blue"));
	
		
		//blue
		//99ÖÖÐ§¹û
/*	         
        //v0.4 
//		filterArray.add(new FilterInfo(R.drawable.video_filter1, new VideoFilter(VideoFilter.VIDEO_TYPE.VIDEO_STAGGERED)));
//		filterArray.add(new FilterInfo(R.drawable.video_filter2, new VideoFilter(VideoFilter.VIDEO_TYPE.VIDEO_TRIPED)));
//		filterArray.add(new FilterInfo(R.drawable.video_filter3, new VideoFilter(VideoFilter.VIDEO_TYPE.VIDEO_3X3)));
//		filterArray.add(new FilterInfo(R.drawable.video_filter4, new VideoFilter(VideoFilter.VIDEO_TYPE.VIDEO_DOTS)));
//		filterArray.add(new FilterInfo(R.drawable.tilereflection_filter1, new TileReflectionFilter(20, 8, 45, (byte)1)));
//		filterArray.add(new FilterInfo(R.drawable.tilereflection_filter2, new TileReflectionFilter(20, 8, 45, (byte)2)));
//		filterArray.add(new FilterInfo(R.drawable.fillpattern_filter, new FillPatternFilter(ImageFilterMain.this, R.drawable.texture1)));
//		filterArray.add(new FilterInfo(R.drawable.fillpattern_filter1, new FillPatternFilter(ImageFilterMain.this, R.drawable.texture2)));
//		filterArray.add(new FilterInfo(R.drawable.mirror_filter1, new MirrorFilter(true)));
//		filterArray.add(new FilterInfo(R.drawable.mirror_filter2, new MirrorFilter(false)));
//		filterArray.add(new FilterInfo(R.drawable.ycb_crlinear_filter, new YCBCrLinearFilter(new YCBCrLinearFilter.Range(-0.3f, 0.3f))));
//		filterArray.add(new FilterInfo(R.drawable.ycb_crlinear_filter2, new YCBCrLinearFilter(new YCBCrLinearFilter.Range(-0.276f, 0.163f), new YCBCrLinearFilter.Range(-0.202f, 0.5f))));
//		filterArray.add(new FilterInfo(R.drawable.filter_orig, new TexturerFilter(new CloudsTexture(), 0.8f, 0.8f),"Cloud Texture"));
		//filterArray.add(new FilterInfo(R.drawable.filter_orig, new TexturerFilter(new LabyrinthTexture(), 0.8f, 0.8f)));
		//filterArray.add(new FilterInfo(R.drawable.filter_orig, new TexturerFilter(new MarbleTexture(), 1.8f, 0.8f)));
//		filterArray.add(new FilterInfo(R.drawable.filter_orig, new TexturerFilter(new WoodTexture(), 0.8f, 0.8f)));
//		filterArray.add(new FilterInfo(R.drawable.texturer_filter4, new TexturerFilter(new TextileTexture(), 0.8f, 0.8f)));
//		filterArray.add(new FilterInfo(R.drawable.hslmodify_filter, new HslModifyFilter(20f)));
		//filterArray.add(new FilterInfo(R.drawable.filter_orig,  new HslModifyFilter(40f)));
//		filterArray.add(new FilterInfo(R.drawable.hslmodify_filter1, new HslModifyFilter(60f)));
//		filterArray.add(new FilterInfo(R.drawable.hslmodify_filter2, new HslModifyFilter(80f)));
		filterArray.add(new FilterInfo(R.drawable.filter_orig, new HslModifyFilter(100f), "HSL100"));
//		filterArray.add(new FilterInfo(R.drawable.hslmodify_filter4, new HslModifyFilter(150f)));
//		filterArray.add(new FilterInfo(R.drawable.hslmodify_filter5, new HslModifyFilter(200f)));
//		filterArray.add(new FilterInfo(R.drawable.hslmodify_filter6, new HslModifyFilter(250f)));
		filterArray.add(new FilterInfo(R.drawable.filter_orig, new HslModifyFilter(300f),"HSL300"));
//		
//		//v0.3  
//		filterArray.add(new FilterInfo(R.drawable.filter_orig, new ZoomBlurFilter(30), "Focus"));
		filterArray.add(new FilterInfo(R.drawable.filter_orig, new ThreeDGridFilter(16, 100), "Grid"));
//		filterArray.add(new FilterInfo(R.drawable.colortone_filter, new ColorToneFilter(Color.rgb(33, 168, 254), 192)));
//		filterArray.add(new FilterInfo(R.drawable.colortone_filter2, new ColorToneFilter(0x00FF00, 192)));//green
//		filterArray.add(new FilterInfo(R.drawable.colortone_filter3, new ColorToneFilter(0xFF0000, 192)));//blue
//		filterArray.add(new FilterInfo(R.drawable.colortone_filter4, new ColorToneFilter(0x00FFFF, 192)));//yellow
//		filterArray.add(new FilterInfo(R.drawable.softglow_filter, new SoftGlowFilter(10, 0.1f, 0.1f)));
//		filterArray.add(new FilterInfo(R.drawable.tilereflection_filter, new TileReflectionFilter(20, 8)));
//		filterArray.add(new FilterInfo(R.drawable.blind_filter1, new BlindFilter(true, 96, 100, 0xffffff)));
//		filterArray.add(new FilterInfo(R.drawable.blind_filter2, new BlindFilter(false, 96, 100, 0x000000)));
//		filterArray.add(new FilterInfo(R.drawable.filter_orig, new RaiseFrameFilter(20),"RAISE"));
//		filterArray.add(new FilterInfo(R.drawable.shift_filter, new ShiftFilter(10)));
//		filterArray.add(new FilterInfo(R.drawable.wave_filter, new WaveFilter(25, 10)));
//		filterArray.add(new FilterInfo(R.drawable.bulge_filter, new BulgeFilter(-97)));
//		filterArray.add(new FilterInfo(R.drawable.twist_filter, new TwistFilter(27, 106)));
//		filterArray.add(new FilterInfo(R.drawable.ripple_filter, new RippleFilter(38, 15, true)));
//		filterArray.add(new FilterInfo(R.drawable.illusion_filter, new IllusionFilter(3)));
//		filterArray.add(new FilterInfo(R.drawable.supernova_filter, new SupernovaFilter(0x00FFFF,20,100)));
//		filterArray.add(new FilterInfo(R.drawable.lensflare_filter, new LensFlareFilter()));
		filterArray.add(new FilterInfo(R.drawable.filter_orig, new PosterizeFilter(2),"Posterize"));
//		filterArray.add(new FilterInfo(R.drawable.gamma_filter, new GammaFilter(50)));
//		filterArray.add(new FilterInfo(R.drawable.sharp_filter, new SharpFilter()));
//		
//		//v0.2
//		filterArray.add(new FilterInfo(R.drawable.invert_filter, new ComicFilter()));
		filterArray.add(new FilterInfo(R.drawable.filter_orig, new SceneFilter(5f, Gradient.Scene()), "SceneGreen"));//green
		filterArray.add(new FilterInfo(R.drawable.filter_orig, new SceneFilter(5f, Gradient.Scene1()), "ScenePurple"));//purple
		filterArray.add(new FilterInfo(R.drawable.filter_orig, new SceneFilter(5f, Gradient.Scene2()), "SceneBlue"));//blue
//		filterArray.add(new FilterInfo(R.drawable.invert_filter, new SceneFilter(5f, Gradient.Scene3())));
		filterArray.add(new FilterInfo(R.drawable.filter_orig, new FilmFilter(80f), "Film"));
//		filterArray.add(new FilterInfo(R.drawable.invert_filter, new FocusFilter()));
//		filterArray.add(new FilterInfo(R.drawable.invert_filter, new CleanGlassFilter()));
//		filterArray.add(new FilterInfo(R.drawable.invert_filter, new PaintBorderFilter(0x00FF00)));//green
//		filterArray.add(new FilterInfo(R.drawable.invert_filter, new PaintBorderFilter(0x00FFFF)));//yellow
//		filterArray.add(new FilterInfo(R.drawable.invert_filter, new PaintBorderFilter(0xFF0000)));//blue
		filterArray.add(new FilterInfo(R.drawable.filter_orig, (IImageFilter) new LomoFilter(), "Lomo"));
//		
//		//v0.1
		filterArray.add(new FilterInfo(R.drawable.filter_orig, new InvertFilter(), "Invert"));
		filterArray.add(new FilterInfo(R.drawable.filter_orig, new BlackWhiteFilter(), "Gray"));
//		filterArray.add(new FilterInfo(R.drawable.edge_filter, new EdgeFilter()));
//		filterArray.add(new FilterInfo(R.drawable.pixelate_filter, new PixelateFilter()));
//		filterArray.add(new FilterInfo(R.drawable.neon_filter, new NeonFilter()));
//		filterArray.add(new FilterInfo(R.drawable.bigbrother_filter, new BigBrotherFilter()));
//		filterArray.add(new FilterInfo(R.drawable.monitor_filter, new MonitorFilter()));
//		filterArray.add(new FilterInfo(R.drawable.relief_filter, new ReliefFilter()));
		filterArray.add(new FilterInfo(R.drawable.filter_orig,new BrightContrastFilter(),"Bright"));
		filterArray.add(new FilterInfo(R.drawable.filter_orig,	new SaturationModifyFilter(),"Saturation"));
//		filterArray.add(new FilterInfo(R.drawable.threshold_filter,	new ThresholdFilter()));
//		filterArray.add(new FilterInfo(R.drawable.noisefilter,	new NoiseFilter()));
//		filterArray.add(new FilterInfo(R.drawable.banner_filter1, new BannerFilter(10, true)));
//		filterArray.add(new FilterInfo(R.drawable.banner_filter2, new BannerFilter(10, false)));
//		filterArray.add(new FilterInfo(R.drawable.rectmatrix_filter, new RectMatrixFilter()));
//		filterArray.add(new FilterInfo(R.drawable.blockprint_filter, new BlockPrintFilter()));
//		filterArray.add(new FilterInfo(R.drawable.brick_filter,	new BrickFilter()));
//		filterArray.add(new FilterInfo(R.drawable.gaussianblur_filter,	new GaussianBlurFilter()));
		filterArray.add(new FilterInfo(R.drawable.filter_orig,	new LightFilter(), "Light"));
//		filterArray.add(new FilterInfo(R.drawable.mosaic_filter,new MistFilter()));
//		filterArray.add(new FilterInfo(R.drawable.mosaic_filter,new MosaicFilter()));
		filterArray.add(new FilterInfo(R.drawable.filter_orig,	new OilPaintFilter(), "Oil"));
//		filterArray.add(new FilterInfo(R.drawable.radialdistortion_filter,new RadialDistortionFilter()));
//		filterArray.add(new FilterInfo(R.drawable.reflection1_filter,new ReflectionFilter(true)));
//		filterArray.add(new FilterInfo(R.drawable.reflection2_filter,new ReflectionFilter(false)));
//		filterArray.add(new FilterInfo(R.drawable.saturationmodify_filter,	new SaturationModifyFilter()));
//		filterArray.add(new FilterInfo(R.drawable.smashcolor_filter,new SmashColorFilter()));
//		filterArray.add(new FilterInfo(R.drawable.tint_filter,	new TintFilter()));
//		filterArray.add(new FilterInfo(R.drawable.vignette_filter,	new VignetteFilter()));
//		filterArray.add(new FilterInfo(R.drawable.autoadjust_filter,new AutoAdjustFilter()));
//		filterArray.add(new FilterInfo(R.drawable.colorquantize_filter,	new ColorQuantizeFilter()));
		filterArray.add(new FilterInfo(R.drawable.filter_orig,	new WaterWaveFilter(), "WaterWave"));
		filterArray.add(new FilterInfo(R.drawable.filter_orig,new VintageFilter(),"Vintage"));
		filterArray.add(new FilterInfo(R.drawable.filter_orig,new OldPhotoFilter(), "Old"));
		filterArray.add(new FilterInfo(R.drawable.filter_orig,	new SepiaFilter(), "Sepia"));
		filterArray.add(new FilterInfo(R.drawable.filter_orig,new RainBowFilter(), "Rainbow"));
//		filterArray.add(new FilterInfo(R.drawable.feather_filter,new FeatherFilter()));
//		filterArray.add(new FilterInfo(R.drawable.xradiation_filter,new XRadiationFilter()));
		filterArray.add(new FilterInfo(R.drawable.filter_orig,new NightVisionFilter(), "Night"));
//
*/
	}
}
