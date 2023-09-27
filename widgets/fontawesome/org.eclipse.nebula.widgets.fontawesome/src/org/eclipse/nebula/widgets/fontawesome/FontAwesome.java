/*******************************************************************************
 * Copyright (c) 2020 Patrik Dufresne (http://www.patrikdufresne.com/).
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors: 
 * Patrik Dufresne (info at patrikdufresne dot com) - initial API and implementation
 * Laurent Caron (laurent dot caron at gmail dot com) - migration to the Nebula Project
 * 
 *******************************************************************************/
package org.eclipse.nebula.widgets.fontawesome;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Display;

/**
 * Utility class used to load the Font Awesome font.
 */
public class FontAwesome {

	public static final String adjust = "\uf042";
	public static final String adn = "\uf170";
	public static final String align_center = "\uf037";
	public static final String align_justify = "\uf039";
	public static final String align_left = "\uf036";
	public static final String align_right = "\uf038";
	public static final String ambulance = "\uf0f9";
	public static final String anchor = "\uf13d";
	public static final String android = "\uf17b";
	public static final String angellist = "\uf209";
	public static final String angle_double_down = "\uf103";
	public static final String angle_double_left = "\uf100";
	public static final String angle_double_right = "\uf101";
	public static final String angle_double_up = "\uf102";
	public static final String angle_down = "\uf107";
	public static final String angle_left = "\uf104";
	public static final String angle_right = "\uf105";
	public static final String angle_up = "\uf106";
	public static final String apple = "\uf179";
	public static final String archive = "\uf187";
	public static final String area_chart = "\uf1fe";
	public static final String arrow_circle_down = "\uf0ab";
	public static final String arrow_circle_left = "\uf0a8";
	public static final String arrow_circle_o_down = "\uf01a";
	public static final String arrow_circle_o_left = "\uf190";
	public static final String arrow_circle_o_right = "\uf18e";
	public static final String arrow_circle_o_up = "\uf01b";
	public static final String arrow_circle_right = "\uf0a9";
	public static final String arrow_circle_up = "\uf0aa";
	public static final String arrow_down = "\uf063";
	public static final String arrow_left = "\uf060";
	public static final String arrow_right = "\uf061";
	public static final String arrow_up = "\uf062";
	public static final String arrows = "\uf047";
	public static final String arrows_alt = "\uf0b2";
	public static final String arrows_h = "\uf07e";
	public static final String arrows_v = "\uf07d";
	public static final String asterisk = "\uf069";
	public static final String at = "\uf1fa";
	public static final String automobile = "\uf1b9";
	public static final String backward = "\uf04a";
	public static final String ban = "\uf05e";
	public static final String bank = "\uf19c";
	public static final String bar_chart = "\uf080";
	public static final String bar_chart_o = "\uf080";
	public static final String barcode = "\uf02a";
	public static final String bars = "\uf0c9";
	public static final String bed = "\uf236";
	public static final String beer = "\uf0fc";
	public static final String behance = "\uf1b4";
	public static final String behance_square = "\uf1b5";
	public static final String bell = "\uf0f3";
	public static final String bell_o = "\uf0a2";
	public static final String bell_slash = "\uf1f6";
	public static final String bell_slash_o = "\uf1f7";
	public static final String bicycle = "\uf206";
	public static final String binoculars = "\uf1e5";
	public static final String birthday_cake = "\uf1fd";
	public static final String bitbucket = "\uf171";
	public static final String bitbucket_square = "\uf172";
	public static final String bitcoin = "\uf15a";
	public static final String bold = "\uf032";
	public static final String bolt = "\uf0e7";
	public static final String bomb = "\uf1e2";
	public static final String book = "\uf02d";
	public static final String bookmark = "\uf02e";
	public static final String bookmark_o = "\uf097";
	public static final String briefcase = "\uf0b1";
	public static final String btc = "\uf15a";
	public static final String bug = "\uf188";
	public static final String building = "\uf1ad";
	public static final String building_o = "\uf0f7";
	public static final String bullhorn = "\uf0a1";
	public static final String bullseye = "\uf140";
	public static final String bus = "\uf207";
	public static final String buysellads = "\uf20d";
	public static final String cab = "\uf1ba";
	public static final String calculator = "\uf1ec";
	public static final String calendar = "\uf073";
	public static final String calendar_o = "\uf133";
	public static final String camera = "\uf030";
	public static final String camera_retro = "\uf083";
	public static final String car = "\uf1b9";
	public static final String caret_down = "\uf0d7";
	public static final String caret_left = "\uf0d9";
	public static final String caret_right = "\uf0da";
	public static final String caret_square_o_down = "\uf150";
	public static final String caret_square_o_left = "\uf191";
	public static final String caret_square_o_right = "\uf152";
	public static final String caret_square_o_up = "\uf151";
	public static final String caret_up = "\uf0d8";
	public static final String cart_arrow_down = "\uf218";
	public static final String cart_plus = "\uf217";
	public static final String cc = "\uf20a";
	public static final String cc_amex = "\uf1f3";
	public static final String cc_discover = "\uf1f2";
	public static final String cc_mastercard = "\uf1f1";
	public static final String cc_paypal = "\uf1f4";
	public static final String cc_stripe = "\uf1f5";
	public static final String cc_visa = "\uf1f0";
	public static final String certificate = "\uf0a3";
	public static final String chain = "\uf0c1";
	public static final String chain_broken = "\uf127";
	public static final String check = "\uf00c";
	public static final String check_circle = "\uf058";
	public static final String check_circle_o = "\uf05d";
	public static final String check_square = "\uf14a";
	public static final String check_square_o = "\uf046";
	public static final String chevron_circle_down = "\uf13a";
	public static final String chevron_circle_left = "\uf137";
	public static final String chevron_circle_right = "\uf138";
	public static final String chevron_circle_up = "\uf139";
	public static final String chevron_down = "\uf078";
	public static final String chevron_left = "\uf053";
	public static final String chevron_right = "\uf054";
	public static final String chevron_up = "\uf077";
	public static final String child = "\uf1ae";
	public static final String circle = "\uf111";
	public static final String circle_o = "\uf10c";
	public static final String circle_o_notch = "\uf1ce";
	public static final String circle_thin = "\uf1db";
	public static final String clipboard = "\uf0ea";
	public static final String clock_o = "\uf017";
	public static final String close = "\uf00d";
	public static final String cloud = "\uf0c2";
	public static final String cloud_download = "\uf0ed";
	public static final String cloud_upload = "\uf0ee";
	public static final String cny = "\uf157";
	public static final String code = "\uf121";
	public static final String code_fork = "\uf126";
	public static final String codepen = "\uf1cb";
	public static final String coffee = "\uf0f4";
	public static final String cog = "\uf013";
	public static final String cogs = "\uf085";
	public static final String columns = "\uf0db";
	public static final String comment = "\uf075";
	public static final String comment_o = "\uf0e5";
	public static final String comments = "\uf086";
	public static final String comments_o = "\uf0e6";
	public static final String compass = "\uf14e";
	public static final String compress = "\uf066";
	public static final String connectdevelop = "\uf20e";
	public static final String copy = "\uf0c5";
	public static final String copyright = "\uf1f9";
	public static final String credit_card = "\uf09d";
	public static final String crop = "\uf125";
	public static final String crosshairs = "\uf05b";
	public static final String css3 = "\uf13c";
	public static final String cube = "\uf1b2";
	public static final String cubes = "\uf1b3";
	public static final String cut = "\uf0c4";
	public static final String cutlery = "\uf0f5";
	public static final String dashboard = "\uf0e4";
	public static final String dashcube = "\uf210";
	public static final String database = "\uf1c0";
	public static final String dedent = "\uf03b";
	private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;
	private static final int DEFAULT_FONT_SIZE = 14;
	public static final String delicious = "\uf1a5";
	public static final String desktop = "\uf108";
	public static final String deviantart = "\uf1bd";
	public static final String diamond = "\uf219";
	public static final String digg = "\uf1a6";
	public static final String dollar = "\uf155";
	public static final String dot_circle_o = "\uf192";
	public static final String download = "\uf019";
	public static final String dribbble = "\uf17d";
	public static final String dropbox = "\uf16b";
	public static final String drupal = "\uf1a9";
	public static final String edit = "\uf044";
	public static final String eject = "\uf052";
	public static final String ellipsis_h = "\uf141";
	public static final String ellipsis_v = "\uf142";
	public static final String empire = "\uf1d1";
	public static final String envelope = "\uf0e0";
	public static final String envelope_o = "\uf003";
	public static final String envelope_square = "\uf199";
	private static final int EOF = -1;
	public static final String eraser = "\uf12d";
	public static final String eur = "\uf153";
	public static final String euro = "\uf153";
	public static final String exchange = "\uf0ec";
	public static final String exclamation = "\uf12a";
	public static final String exclamation_circle = "\uf06a";
	public static final String exclamation_triangle = "\uf071";
	public static final String expand = "\uf065";
	public static final String external_link = "\uf08e";
	public static final String external_link_square = "\uf14c";
	public static final String eye = "\uf06e";
	public static final String eye_slash = "\uf070";
	public static final String eyedropper = "\uf1fb";
	public static final String facebook = "\uf09a";
	public static final String facebook_f = "\uf09a";
	public static final String facebook_official = "\uf230";
	public static final String facebook_square = "\uf082";
	public static final String fast_backward = "\uf049";
	public static final String fast_forward = "\uf050";
	public static final String fax = "\uf1ac";
	public static final String female = "\uf182";
	public static final String fighter_jet = "\uf0fb";
	public static final String file = "\uf15b";
	public static final String file_archive_o = "\uf1c6";
	public static final String file_audio_o = "\uf1c7";
	public static final String file_code_o = "\uf1c9";
	public static final String file_excel_o = "\uf1c3";
	public static final String file_image_o = "\uf1c5";
	public static final String file_movie_o = "\uf1c8";
	public static final String file_o = "\uf016";
	public static final String file_pdf_o = "\uf1c1";
	public static final String file_photo_o = "\uf1c5";
	public static final String file_picture_o = "\uf1c5";
	public static final String file_powerpoint_o = "\uf1c4";
	public static final String file_sound_o = "\uf1c7";
	public static final String file_text = "\uf15c";
	public static final String file_text_o = "\uf0f6";
	public static final String file_video_o = "\uf1c8";
	public static final String file_word_o = "\uf1c2";
	public static final String file_zip_o = "\uf1c6";
	public static final String files_o = "\uf0c5";
	public static final String film = "\uf008";
	public static final String filter = "\uf0b0";
	public static final String fire = "\uf06d";
	public static final String fire_extinguisher = "\uf134";
	public static final String flag = "\uf024";
	public static final String flag_checkered = "\uf11e";
	public static final String flag_o = "\uf11d";
	public static final String flash = "\uf0e7";
	public static final String flask = "\uf0c3";
	public static final String flickr = "\uf16e";
	public static final String floppy_o = "\uf0c7";
	public static final String folder = "\uf07b";
	public static final String folder_o = "\uf114";
	public static final String folder_open = "\uf07c";
	public static final String folder_open_o = "\uf115";
	public static final String font = "\uf031";
	/**
	 * Symbolic name used to store the font awesome.
	 */
	public static final String forumbee = "\uf211";
	public static final String forward = "\uf04e";
	public static final String foursquare = "\uf180";
	public static final String frown_o = "\uf119";
	public static final String futbol_o = "\uf1e3";
	public static final String gamepad = "\uf11b";
	public static final String gavel = "\uf0e3";
	public static final String gbp = "\uf154";
	public static final String ge = "\uf1d1";
	public static final String gear = "\uf013";
	public static final String gears = "\uf085";
	public static final String genderless = "\uf1db";
	public static final String gift = "\uf06b";
	public static final String git = "\uf1d3";
	public static final String git_square = "\uf1d2";
	public static final String github = "\uf09b";
	public static final String github_alt = "\uf113";
	public static final String github_square = "\uf092";
	public static final String gittip = "\uf184";
	public static final String glass = "\uf000";
	public static final String globe = "\uf0ac";
	public static final String google = "\uf1a0";
	public static final String google_plus = "\uf0d5";
	public static final String google_plus_square = "\uf0d4";
	public static final String google_wallet = "\uf1ee";
	public static final String graduation_cap = "\uf19d";
	public static final String gratipay = "\uf184";
	public static final String group = "\uf0c0";
	public static final String h_square = "\uf0fd";
	public static final String hacker_news = "\uf1d4";
	public static final String hand_o_down = "\uf0a7";
	public static final String hand_o_left = "\uf0a5";
	public static final String hand_o_right = "\uf0a4";
	public static final String hand_o_up = "\uf0a6";
	public static final String hdd_o = "\uf0a0";
	public static final String header = "\uf1dc";
	public static final String headphones = "\uf025";
	public static final String heart = "\uf004";
	public static final String heart_o = "\uf08a";
	public static final String heartbeat = "\uf21e";
	public static final String history = "\uf1da";
	public static final String home = "\uf015";
	public static final String hospital_o = "\uf0f8";
	public static final String hotel = "\uf236";
	public static final String html5 = "\uf13b";
	public static final String ils = "\uf20b";
	public static final String image = "\uf03e";
	public static final String inbox = "\uf01c";
	public static final String indent = "\uf03c";
	public static final String info = "\uf129";
	public static final String info_circle = "\uf05a";
	public static final String inr = "\uf156";
	public static final String instagram = "\uf16d";
	public static final String institution = "\uf19c";
	public static final String ioxhost = "\uf208";
	public static final String italic = "\uf033";
	public static final String joomla = "\uf1aa";
	public static final String jpy = "\uf157";
	public static final String jsfiddle = "\uf1cc";
	public static final String key = "\uf084";
	public static final String keyboard_o = "\uf11c";
	public static final String krw = "\uf159";
	public static final String language = "\uf1ab";
	public static final String laptop = "\uf109";
	public static final String lastfm = "\uf202";
	public static final String lastfm_square = "\uf203";
	public static final String leaf = "\uf06c";
	public static final String leanpub = "\uf212";
	public static final String legal = "\uf0e3";
	public static final String lemon_o = "\uf094";
	public static final String level_down = "\uf149";
	public static final String level_up = "\uf148";
	public static final String life_bouy = "\uf1cd";
	public static final String life_buoy = "\uf1cd";
	public static final String life_ring = "\uf1cd";
	public static final String life_saver = "\uf1cd";
	public static final String lightbulb_o = "\uf0eb";
	public static final String line_chart = "\uf201";
	public static final String link = "\uf0c1";
	public static final String linkedin = "\uf0e1";
	public static final String linkedin_square = "\uf08c";
	public static final String linux = "\uf17c";
	public static final String list = "\uf03a";
	public static final String list_alt = "\uf022";
	public static final String list_ol = "\uf0cb";
	public static final String list_ul = "\uf0ca";
	public static final String location_arrow = "\uf124";
	public static final String lock = "\uf023";
	public static final String long_arrow_down = "\uf175";
	public static final String long_arrow_left = "\uf177";
	public static final String long_arrow_right = "\uf178";
	public static final String long_arrow_up = "\uf176";
	public static final String magic = "\uf0d0";
	public static final String magnet = "\uf076";
	public static final String mail_forward = "\uf064";
	public static final String mail_reply = "\uf112";
	public static final String mail_reply_all = "\uf122";
	public static final String male = "\uf183";
	public static final String map_marker = "\uf041";
	public static final String mars = "\uf222";
	public static final String mars_double = "\uf227";
	public static final String mars_stroke = "\uf229";
	public static final String mars_stroke_h = "\uf22b";
	public static final String mars_stroke_v = "\uf22a";
	public static final String maxcdn = "\uf136";
	public static final String meanpath = "\uf20c";
	public static final String medium = "\uf23a";
	public static final String medkit = "\uf0fa";
	public static final String meh_o = "\uf11a";
	public static final String mercury = "\uf223";
	public static final String microphone = "\uf130";
	public static final String microphone_slash = "\uf131";
	public static final String minus = "\uf068";
	public static final String minus_circle = "\uf056";
	public static final String minus_square = "\uf146";
	public static final String minus_square_o = "\uf147";
	public static final String mobile = "\uf10b";
	public static final String mobile_phone = "\uf10b";
	public static final String money = "\uf0d6";
	public static final String moon_o = "\uf186";
	public static final String mortar_board = "\uf19d";
	public static final String motorcycle = "\uf21c";
	public static final String music = "\uf001";
	public static final String navicon = "\uf0c9";
	public static final String neuter = "\uf22c";
	public static final String newspaper_o = "\uf1ea";
	public static final String openid = "\uf19b";
	public static final String outdent = "\uf03b";
	public static final String pagelines = "\uf18c";
	public static final String paint_brush = "\uf1fc";
	public static final String paper_plane = "\uf1d8";
	public static final String paper_plane_o = "\uf1d9";
	public static final String paperclip = "\uf0c6";
	public static final String paragraph = "\uf1dd";
	public static final String paste = "\uf0ea";
	public static final String pause = "\uf04c";
	public static final String paw = "\uf1b0";
	public static final String paypal = "\uf1ed";
	public static final String pencil = "\uf040";
	public static final String pencil_square = "\uf14b";
	public static final String pencil_square_o = "\uf044";
	public static final String phone = "\uf095";
	public static final String phone_square = "\uf098";
	public static final String photo = "\uf03e";
	public static final String picture_o = "\uf03e";
	public static final String pie_chart = "\uf200";
	public static final String pied_piper = "\uf1a7";
	public static final String pied_piper_alt = "\uf1a8";
	public static final String pinterest = "\uf0d2";
	public static final String pinterest_p = "\uf231";
	public static final String pinterest_square = "\uf0d3";
	public static final String plane = "\uf072";
	public static final String play = "\uf04b";
	public static final String play_circle = "\uf144";
	public static final String play_circle_o = "\uf01d";
	public static final String plug = "\uf1e6";
	public static final String plus = "\uf067";
	public static final String plus_circle = "\uf055";
	public static final String plus_square = "\uf0fe";
	public static final String plus_square_o = "\uf196";
	public static final String power_off = "\uf011";
	public static final String print = "\uf02f";
	public static final String puzzle_piece = "\uf12e";
	public static final String qq = "\uf1d6";
	public static final String qrcode = "\uf029";
	public static final String question = "\uf128";
	public static final String question_circle = "\uf059";
	public static final String quote_left = "\uf10d";
	public static final String quote_right = "\uf10e";
	public static final String ra = "\uf1d0";
	public static final String random = "\uf074";
	public static final String rebel = "\uf1d0";
	public static final String recycle = "\uf1b8";
	public static final String reddit = "\uf1a1";
	public static final String reddit_square = "\uf1a2";
	public static final String refresh = "\uf021";
	public static final String remove = "\uf00d";
	public static final String renren = "\uf18b";
	public static final String reorder = "\uf0c9";
	public static final String repeat = "\uf01e";
	public static final String reply = "\uf112";
	public static final String reply_all = "\uf122";
	public static final String retweet = "\uf079";
	public static final String rmb = "\uf157";
	public static final String road = "\uf018";
	public static final String rocket = "\uf135";
	public static final String rotate_left = "\uf0e2";
	public static final String rotate_right = "\uf01e";
	public static final String rouble = "\uf158";
	public static final String rss = "\uf09e";
	public static final String rss_square = "\uf143";
	public static final String rub = "\uf158";
	public static final String ruble = "\uf158";
	public static final String rupee = "\uf156";
	public static final String save = "\uf0c7";
	public static final String scissors = "\uf0c4";
	public static final String search = "\uf002";
	public static final String search_minus = "\uf010";
	public static final String search_plus = "\uf00e";
	public static final String sellsy = "\uf213";
	public static final String send = "\uf1d8";
	public static final String send_o = "\uf1d9";
	public static final String server = "\uf233";
	public static final String share = "\uf064";
	public static final String share_alt = "\uf1e0";
	public static final String share_alt_square = "\uf1e1";
	public static final String share_square = "\uf14d";
	public static final String share_square_o = "\uf045";
	public static final String shekel = "\uf20b";
	public static final String sheqel = "\uf20b";
	public static final String shield = "\uf132";
	public static final String ship = "\uf21a";
	public static final String shirtsinbulk = "\uf214";
	public static final String shopping_cart = "\uf07a";
	public static final String sign_in = "\uf090";
	public static final String sign_out = "\uf08b";
	public static final String signal = "\uf012";
	public static final String simplybuilt = "\uf215";
	public static final String sitemap = "\uf0e8";
	public static final String skyatlas = "\uf216";
	public static final String skype = "\uf17e";
	public static final String slack = "\uf198";
	public static final String sliders = "\uf1de";
	public static final String slideshare = "\uf1e7";
	public static final String smile_o = "\uf118";
	public static final String soccer_ball_o = "\uf1e3";
	public static final String sort = "\uf0dc";
	public static final String sort_alpha_asc = "\uf15d";
	public static final String sort_alpha_desc = "\uf15e";
	public static final String sort_amount_asc = "\uf160";
	public static final String sort_amount_desc = "\uf161";
	public static final String sort_asc = "\uf0de";
	public static final String sort_desc = "\uf0dd";
	public static final String sort_down = "\uf0dd";
	public static final String sort_numeric_asc = "\uf162";
	public static final String sort_numeric_desc = "\uf163";
	public static final String sort_up = "\uf0de";
	public static final String soundcloud = "\uf1be";
	public static final String space_shuttle = "\uf197";
	public static final String spinner = "\uf110";
	public static final String spoon = "\uf1b1";
	public static final String spotify = "\uf1bc";
	public static final String square = "\uf0c8";
	public static final String square_o = "\uf096";
	public static final String stack_exchange = "\uf18d";
	public static final String stack_overflow = "\uf16c";
	public static final String star = "\uf005";
	public static final String star_half = "\uf089";
	public static final String star_half_empty = "\uf123";
	public static final String star_half_full = "\uf123";
	public static final String star_half_o = "\uf123";
	public static final String star_o = "\uf006";
	public static final String steam = "\uf1b6";
	public static final String steam_square = "\uf1b7";
	public static final String step_backward = "\uf048";
	public static final String step_forward = "\uf051";
	public static final String stethoscope = "\uf0f1";
	public static final String stop = "\uf04d";
	public static final String street_view = "\uf21d";
	public static final String strikethrough = "\uf0cc";
	public static final String stumbleupon = "\uf1a4";
	public static final String stumbleupon_circle = "\uf1a3";
	public static final String subscript = "\uf12c";
	public static final String subway = "\uf239";
	public static final String suitcase = "\uf0f2";
	public static final String sun_o = "\uf185";
	public static final String superscript = "\uf12b";
	public static final String support = "\uf1cd";
	public static final String table = "\uf0ce";
	public static final String tablet = "\uf10a";
	public static final String tachometer = "\uf0e4";
	public static final String tag = "\uf02b";
	public static final String tags = "\uf02c";
	public static final String tasks = "\uf0ae";
	public static final String taxi = "\uf1ba";
	public static final String tencent_weibo = "\uf1d5";
	public static final String terminal = "\uf120";
	public static final String text_height = "\uf034";
	public static final String text_width = "\uf035";
	public static final String th = "\uf00a";
	public static final String th_large = "\uf009";
	public static final String th_list = "\uf00b";
	public static final String thumb_tack = "\uf08d";
	public static final String thumbs_down = "\uf165";
	public static final String thumbs_o_down = "\uf088";
	public static final String thumbs_o_up = "\uf087";
	public static final String thumbs_up = "\uf164";
	public static final String ticket = "\uf145";
	public static final String times = "\uf00d";
	public static final String times_circle = "\uf057";
	public static final String times_circle_o = "\uf05c";
	public static final String tint = "\uf043";
	public static final String toggle_down = "\uf150";
	public static final String toggle_left = "\uf191";
	public static final String toggle_off = "\uf204";
	public static final String toggle_on = "\uf205";
	public static final String toggle_right = "\uf152";
	public static final String toggle_up = "\uf151";
	public static final String train = "\uf238";
	public static final String transgender = "\uf224";
	public static final String transgender_alt = "\uf225";
	public static final String trash = "\uf1f8";
	public static final String trash_o = "\uf014";
	public static final String tree = "\uf1bb";
	public static final String trello = "\uf181";
	public static final String trophy = "\uf091";
	public static final String truck = "\uf0d1";
	public static final String TRY = "\uf195";
	public static final String tty = "\uf1e4";
	public static final String tumblr = "\uf173";
	public static final String tumblr_square = "\uf174";
	public static final String turkish_lira = "\uf195";
	public static final String twitch = "\uf1e8";
	public static final String twitter = "\uf099";
	public static final String twitter_square = "\uf081";
	public static final String umbrella = "\uf0e9";
	public static final String underline = "\uf0cd";
	public static final String undo = "\uf0e2";
	public static final String university = "\uf19c";
	public static final String unlink = "\uf127";
	public static final String unlock = "\uf09c";
	public static final String unlock_alt = "\uf13e";
	public static final String unsorted = "\uf0dc";
	public static final String upload = "\uf093";
	public static final String usd = "\uf155";
	public static final String user = "\uf007";
	public static final String user_md = "\uf0f0";
	public static final String user_plus = "\uf234";
	public static final String user_secret = "\uf21b";
	public static final String user_times = "\uf235";
	public static final String users = "\uf0c0";
	public static final String venus = "\uf221";
	public static final String venus_double = "\uf226";
	public static final String venus_mars = "\uf228";
	/**
	 * Version used when developing.
	 */
	private static final String VERSION_DEV = "DEV";
	public static final String viacoin = "\uf237";
	public static final String video_camera = "\uf03d";
	public static final String vimeo_square = "\uf194";
	public static final String vine = "\uf1ca";
	public static final String vk = "\uf189";
	public static final String volume_down = "\uf027";
	public static final String volume_off = "\uf026";
	public static final String volume_up = "\uf028";
	public static final String warning = "\uf071";
	public static final String wechat = "\uf1d7";
	public static final String weibo = "\uf18a";
	public static final String weixin = "\uf1d7";
	public static final String whatsapp = "\uf232";
	public static final String wheelchair = "\uf193";
	public static final String wifi = "\uf1eb";
	public static final String windows = "\uf17a";
	public static final String won = "\uf159";
	public static final String wordpress = "\uf19a";
	public static final String wrench = "\uf0ad";
	public static final String xing = "\uf168";
	public static final String xing_square = "\uf169";
	public static final String yahoo = "\uf19e";
	public static final String yelp = "\uf1e9";
	public static final String yen = "\uf157";
	public static final String youtube = "\uf167";
	public static final String youtube_play = "\uf16a";
	public static final String youtube_square = "\uf166";

	private static Map<Integer, Font> fonts = new HashMap<>();

	private static long copy(InputStream input, OutputStream output, byte[] buffer) throws IOException {
		long count = 0;
		int n = 0;
		while (EOF != (n = input.read(buffer))) {
			output.write(buffer, 0, n);
			count += n;
		}
		return count;
	}

	/**
	 * Return the current version.
	 * 
	 * @return
	 */
	private static String getCurrentVersion() {
		// Get the version from the package manifest
		String version = FontAwesome.class.getPackage().getImplementationVersion();
		if (version == null) {
			return VERSION_DEV;
		}
		return version;
	}

	/**
	 * Return a FontAwesome font for SWT.
	 * 
	 * @return the font or null.
	 */
	public static Font getFont() {
		if (fonts.containsKey(DEFAULT_FONT_SIZE)) {
			return fonts.get(DEFAULT_FONT_SIZE);
		}
		if (!loadFont()) {
			return null;
		}
		FontData[] data = new FontData[] { new FontData("fontawesome", DEFAULT_FONT_SIZE, SWT.NORMAL) };
		fonts.put(DEFAULT_FONT_SIZE, new Font(Display.getDefault(), data));
		return fonts.get(DEFAULT_FONT_SIZE);
	}

	/**
	 * Return a FontAwesome font for SWT.
	 * 
	 * @param size
	 * @return
	 */
	public static Font getFont(int size) {
		if (!fonts.containsKey(size)) {
			// GetFont() may return null, so handle this case.
			Font font = getFont();
			if (font == null) {
				return null;
			}
			FontData[] data = font.getFontData();
			for (FontData d : data) {
				d.setHeight(size);
			}
			fonts.put(size, new Font(Display.getDefault(), data));
		}
		return fonts.get(size);
	}

	/**
	 * Load the font from resources.
	 * 
	 * @return
	 */
	private static boolean loadFont() {
		// Get file from classpath.

		// Add dispose listener
		Display.getDefault().addListener(SWT.Dispose, e -> {
			for (Font font : fonts.values()) {
				if (!font.isDisposed())
					font.dispose();
			}
		});

		try {
			// Copy file to temp diretory.
			String temp = System.getProperty("java.io.tmpdir");
			final File tempfile = new File(temp,
					System.getProperty("user.name") + "-fontawesome-webfont-" + getCurrentVersion() + ".ttf");
			tempfile.deleteOnExit();
			try (FileOutputStream out = new FileOutputStream(tempfile);
					InputStream in = FontAwesome.class.getResourceAsStream("resources/fontawesome-webfont.ttf");) {
				if (in == null) {
					return false;
				}
				copy(in, out, new byte[DEFAULT_BUFFER_SIZE]);
			}
			// Load the font.
			return Display.getDefault().loadFont(tempfile.getAbsolutePath());
		} catch (IOException e) {
			// This should rarely happen, but clearly, when this happen we need
			// to print something to a log file. Otherwise there is no way to debug this.
			e.printStackTrace();
			return false;
		}
	}
}