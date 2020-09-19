package org.music.similarity;

import org.music.data.ArtistData;
import org.music.data.LabelData;
import org.music.data.ReleaseLabel;
import org.music.decay.AgreementDecay;
import org.music.decay.DecayProcessor;
import org.music.decay.DisagreementDecay;

import java.util.ArrayList;

public class RecordSimilarity {

	public AgreementDecay nameagrdecay;
	public AgreementDecay countryagrdecay;
	public AgreementDecay styleagrdecay;
	public AgreementDecay releaseagrdecay;
	public AgreementDecay genresagrdecay;
	public AgreementDecay artistagrdecay;
	public AgreementDecay labelagrdecay;
	public AgreementDecay extraagrdecay;
	public AgreementDecay catnoagrdecay;
	public AgreementDecay formatagrdecay;

	public DisagreementDecay namedisdecay;
	public DisagreementDecay countrydisdecay;
	public DisagreementDecay styledisdecay;
	public DisagreementDecay releasedisdecay;
	public DisagreementDecay genresdisdecay;
	public DisagreementDecay artistdisdecay;
	public DisagreementDecay labeldisdecay;
	public DisagreementDecay extradisdecay;
	public DisagreementDecay catnodisdecay;
	public DisagreementDecay formatdisdecay;

	public AttributeSimilarity attrSim;


	private double th = 0.8;
	private double th2 = 0.5;

	public RecordSimilarity() {
	}

	;

	public RecordSimilarity(String sql, String entity) throws Exception {
		DecayProcessor decayprocessor = new DecayProcessor(sql, entity);
		nameagrdecay = new AgreementDecay();
		countryagrdecay = new AgreementDecay();
		styleagrdecay = new AgreementDecay();
		releaseagrdecay = new AgreementDecay();
		genresagrdecay = new AgreementDecay();
		artistagrdecay = new AgreementDecay();
		labelagrdecay = new AgreementDecay();
		extraagrdecay = new AgreementDecay();
		catnoagrdecay = new AgreementDecay();
		formatagrdecay = new AgreementDecay();

		namedisdecay = new DisagreementDecay();
		countrydisdecay = new DisagreementDecay();
		styledisdecay = new DisagreementDecay();
		releasedisdecay = new DisagreementDecay();
		genresdisdecay = new DisagreementDecay();
		artistdisdecay = new DisagreementDecay();
		labeldisdecay = new DisagreementDecay();
		extradisdecay = new DisagreementDecay();
		catnodisdecay = new DisagreementDecay();
		formatdisdecay = new DisagreementDecay();

		if (entity.equalsIgnoreCase("label")) {
			System.out.println("+++ name  agreement decay");
			decayprocessor.process("name", nameagrdecay);
			System.out.println("+++ country  agreement decay");
			decayprocessor.process("country", countryagrdecay);
			System.out.println("+++ styles  agreement decay");
			decayprocessor.process("styles", styleagrdecay);
			System.out.println("+++ release  agreement decay");
			decayprocessor.process("release", releaseagrdecay);
			System.out.println("+++ genres  agreement decay");
			decayprocessor.process("genres", genresagrdecay);
			System.out.println("+++ artist  agreement decay");
			decayprocessor.process("artist", artistagrdecay);

			System.out.println("+++ name  disagreement decay");
			decayprocessor.process("name", namedisdecay);
			System.out.println("+++ country  disagreement decay");
			decayprocessor.process("country", countrydisdecay);
			System.out.println("+++ styles  disagreement decay");
			decayprocessor.process("styles", styledisdecay);
			System.out.println("+++ release  disagreement decay");
			decayprocessor.process("release", releasedisdecay);
			System.out.println("+++ genres  disagreement decay");
			decayprocessor.process("genres", genresdisdecay);
			System.out.println("+++ artist  disagreement decay");
			decayprocessor.process("artist", artistdisdecay);
		} else if (entity.equalsIgnoreCase("releaselabel")) {
			System.out.println("+++ label  agreement decay");
			decayprocessor.process("label", labelagrdecay);
			System.out.println("+++ country  agreement decay");
			decayprocessor.process("country", countryagrdecay);
			System.out.println("+++ styles  agreement decay");
			decayprocessor.process("styles", styleagrdecay);
			System.out.println("+++ release  agreement decay");
			decayprocessor.process("release", releaseagrdecay);
			System.out.println("+++ genres  agreement decay");
			decayprocessor.process("genres", genresagrdecay);
			System.out.println("+++ artist  agreement decay");
			decayprocessor.process("artist", artistagrdecay);
			System.out.println("+++ extrartist  agreement decay");
			decayprocessor.process("extrartist", extraagrdecay);
			System.out.println("+++ catno  agreement decay");
			decayprocessor.process("catno", catnoagrdecay);
			System.out.println("+++ format  agreement decay");
			decayprocessor.process("format", formatagrdecay);

			System.out.println("+++ label  disagreement decay");
			decayprocessor.process("label", labeldisdecay);
			System.out.println("+++ country  disagreement decay");
			decayprocessor.process("country", countrydisdecay);
			System.out.println("+++ styles  disagreement decay");
			decayprocessor.process("styles", styledisdecay);
			System.out.println("+++ release  disagreement decay");
			decayprocessor.process("release", releasedisdecay);
			System.out.println("+++ genres  disagreement decay");
			decayprocessor.process("genres", genresdisdecay);
			System.out.println("+++ artist  disagreement decay");
			decayprocessor.process("artist", artistdisdecay);
			System.out.println("+++ extrartist  disagreement decay");
			decayprocessor.process("extrartist", extradisdecay);
			System.out.println("+++ catno  disagreement decay");
			decayprocessor.process("catno", catnodisdecay);
			System.out.println("+++ format  disagreement decay");
			decayprocessor.process("format", formatdisdecay);
		}

		attrSim = new AttributeSimilarity();
	}

	public RecordSimilarity(String sql) throws Exception {
		DecayProcessor decayprocessor = new DecayProcessor(sql);
		nameagrdecay = new AgreementDecay();
		countryagrdecay = new AgreementDecay();
		styleagrdecay = new AgreementDecay();
		releaseagrdecay = new AgreementDecay();
		genresagrdecay = new AgreementDecay();

		namedisdecay = new DisagreementDecay();
		countrydisdecay = new DisagreementDecay();
		styledisdecay = new DisagreementDecay();
		releasedisdecay = new DisagreementDecay();
		genresdisdecay = new DisagreementDecay();

		System.out.println("+++ name  agreement decay");
		decayprocessor.process("name", nameagrdecay);
		System.out.println("+++ country  agreement decay");
		decayprocessor.process("country", countryagrdecay);
		System.out.println("+++ styles  agreement decay");
		decayprocessor.process("styles", styleagrdecay);
		System.out.println("+++ release  agreement decay");
		decayprocessor.process("release", releaseagrdecay);
		System.out.println("+++ genres  agreement decay");
		decayprocessor.process("genres", genresagrdecay);

		System.out.println("+++ name  disagreement decay");
		decayprocessor.process("name", namedisdecay);
		System.out.println("+++ country  disagreement decay");
		decayprocessor.process("country", countrydisdecay);
		System.out.println("+++ styles  disagreement decay");
		decayprocessor.process("styles", styledisdecay);
		System.out.println("+++ release  disagreement decay");
		decayprocessor.process("release", releasedisdecay);
		System.out.println("+++ genres  disagreement decay");
		decayprocessor.process("genres", genresdisdecay);

		attrSim = new AttributeSimilarity();
	}

	;

	public double labelTimelessSimilarity(LabelData a1, LabelData a2) {
		double score = 0;

		//System.out.println("compare \t"+a1.getGenres()+"\t"+a2.getGenres());

		int yr1 = Integer.parseInt(a1.getDate());
		int yr2 = Integer.parseInt(a2.getDate());
		int t = Math.abs(yr1 - yr2);

		//System.out.println("\t\t"+yr1+"\t"+yr2);


		String name1 = a1.getName();
		String name2 = a2.getName();
		double namesim = attrSim.StrSim(name1, name2);
		double nameweight = 1;


		//System.out.println("name \t"+name1+"\t"+name2+"\t"+namesim+"\t"+nameweight);

		String artist1 = a1.getArtist();
		String artist2 = a2.getArtist();
		double artistsim = attrSim.StrSim(artist1, artist2);
		double artistweight = 1;

		//System.out.println("artist \t"+artist1+"\t"+artist2+"\t"+artistsim+"\t"+artistweight);

		String release1 = a1.getRelease();
		String release2 = a2.getRelease();
		double releasesim = attrSim.StrSim(release1, release2);
		double releaseweight = 1;

		//System.out.println("release \t"+release1+"\t"+release2+"\t"+releasesim+"\t"+releaseweight);

		String country1 = a1.getCountry();
		String country2 = a2.getCountry();
		double countrysim = attrSim.StrSim(country1, country2);
		double countryweight = 1;
		//System.out.println("country \t"+country1+"\t"+country2+"\t"+countrysim+"\t"+countryweight);

		ArrayList genres1 = a1.getGenres();
		ArrayList genres2 = a2.getGenres();
		double genresim = attrSim.SetSim(genres1, genres2);
		double genresweight = 1;

		//System.out.println("genres \t"+genres1+"\t"+genres2+"\t"+genresim+"\t"+genresweight);

		ArrayList styles1 = a1.getStyle();
		ArrayList styles2 = a2.getStyle();
		double stylesim = attrSim.SetSim(styles1, styles2);
		double styleweight = 1;

		double sumweight = (nameweight + releaseweight + countryweight + genresweight + styleweight + artistweight);

		//System.out.println((namesim*nameweight + releasesim*releaseweight+countrysim*countryweight + genresim * genresweight + stylesim * styleweight)+" \t "+ (nameweight + releaseweight + countryweight + genresweight + styleweight));

		if (sumweight == 0)
			score = 0;
		else
			score = (namesim * nameweight + artistsim * artistweight + releasesim * releaseweight + countrysim * countryweight + genresim * genresweight + stylesim * styleweight) / (nameweight + releaseweight + countryweight + genresweight + styleweight + artistweight);

		return score;
	}

	public double labelDecaySimilarity(LabelData a1, LabelData a2) {
		double score = 0;

		//System.out.println("compare \t"+a1.getGenres()+"\t"+a2.getGenres());

		int yr1 = Integer.parseInt(a1.getDate());
		int yr2 = Integer.parseInt(a2.getDate());
		int t = Math.abs(yr1 - yr2);

		//System.out.println("\t\t"+yr1+"\t"+yr2);

		double nameagr = nameagrdecay.computeAgr(t);
		double countryagr = countryagrdecay.computeAgr(t);
		double styleagr = styleagrdecay.computeAgr(t);
		double releaseagr = releaseagrdecay.computeAgr(t);
		double genresagr = genresagrdecay.computeAgr(t);
		double artistagr = artistagrdecay.computeAgr(t);

		double namedis = namedisdecay.computeDis(t);
		double countrydis = countrydisdecay.computeDis(t);
		double styledis = styledisdecay.computeDis(t);
		double releasedis = releasedisdecay.computeDis(t);
		double genresdis = genresdisdecay.computeDis(t);
		double artistdis = artistdisdecay.computeDis(t);

		//System.out.println("\t\t"+nameagr+"\t"+countryagr+"\t"+styleagr+"\t"+releaseagr+"\t"+genresagr);
		//System.out.println("\t\t"+namedis+"\t"+countrydis+"\t"+styledis+"\t"+releasedis+"\t"+genresdis);

		String name1 = a1.getName();
		String name2 = a2.getName();
		double namesim = attrSim.StrSim(name1, name2);
		double nameweight = 1;
		if (namesim >= th) {
			nameweight = 1 - nameagr;
		} else if (namesim < th2) {
			nameweight = 1 - namedis;
		} else {
			nameweight = namesim * (1 - nameagr) + (1 - namesim) * (1 - namedis);
		}

		//System.out.println("name \t"+name1+"\t"+name2+"\t"+namesim+"\t"+nameweight);

		String artist1 = a1.getArtist();
		String artist2 = a2.getArtist();
		double artistsim = attrSim.StrSim(artist1, artist2);
		double artistweight = 1;
		if (artistsim >= th) {
			artistweight = 1 - artistagr;
		} else if (artistsim < th2) {
			artistweight = 1 - artistdis;
		} else {
			artistweight = artistsim * (1 - artistagr) + (1 - artistsim) * (1 - artistdis);
		}

		//System.out.println("artist \t"+artist1+"\t"+artist2+"\t"+artistsim+"\t"+artistweight);

		String release1 = a1.getRelease();
		String release2 = a2.getRelease();
		double releasesim = attrSim.StrSim(release1, release2);
		double releaseweight = 1;
		if (releasesim >= th) {
			releaseweight = 1 - releaseagr;
		} else if (releasesim < th2) {
			releaseweight = 1 - releasedis;
		} else {
			releaseweight = releasesim * (1 - releaseagr) + (1 - releasesim) * (1 - releasedis);
		}

		System.out.println("release \t" + release1 + "\t" + release2 + "\t" + releasesim + "\t" + releaseweight);

		String country1 = a1.getCountry();
		String country2 = a2.getCountry();
		double countrysim = attrSim.StrSim(country1, country2);
		double countryweight = 1;
		if (countrysim >= th) {
			countryweight = 1 - countryagr;
		} else if (countrysim < th2) {
			countryweight = 1 - countrydis;
		} else {
			countryweight = countrysim * (1 - countryagr) + (1 - countrysim) * (1 - countrydis);
		}
		//System.out.println("country \t"+country1+"\t"+country2+"\t"+countrysim+"\t"+countryweight);

		ArrayList genres1 = a1.getGenres();
		ArrayList genres2 = a2.getGenres();
		double genresim = attrSim.SetSim(genres1, genres2);
		double genresweight = 1;
		if (genresim >= th) {
			genresweight = 1 - genresagr;
		} else if (genresim < th2) {
			genresweight = 1 - genresdis;
		} else {
			genresweight = genresim * (1 - genresagr) + (1 - genresim) * (1 - genresdis);
		}

		//System.out.println("genres \t"+genres1+"\t"+genres2+"\t"+genresim+"\t"+genresweight);

		ArrayList styles1 = a1.getStyle();
		ArrayList styles2 = a2.getStyle();
		double stylesim = attrSim.SetSim(styles1, styles2);
		double styleweight = 1;
		if (stylesim >= th) {
			styleweight = 1 - styleagr;
		} else if (stylesim < th2) {
			styleweight = 1 - styledis;
		} else {
			styleweight = stylesim * (1 - styleagr) + (1 - stylesim) * (1 - styledis);
		}

		double sumweight = (nameweight + releaseweight + countryweight + genresweight + styleweight + artistweight);
		//System.out.println("style \t"+styles1+"\t"+styles2+"\t"+stylesim+"\t"+styleweight);

		//System.out.println((namesim*nameweight + releasesim*releaseweight+countrysim*countryweight + genresim * genresweight + stylesim * styleweight)+" \t "+ (nameweight + releaseweight + countryweight + genresweight + styleweight));

		if (sumweight == 0)
			score = 0;
		else
			score = (namesim * nameweight + artistsim * artistweight + releasesim * releaseweight + countrysim * countryweight + genresim * genresweight + stylesim * styleweight) / (nameweight + releaseweight + countryweight + genresweight + styleweight + artistweight);

		return score;
	}

	public double relabelDecaySimilarity(ReleaseLabel a1, ReleaseLabel a2) {
		double score = 0;

		//System.out.println("compare \t"+a1.getGenres()+"\t"+a2.getGenres());

		int yr1 = a1.getDate();
		int yr2 = a2.getDate();
		int t = Math.abs(yr1 - yr2);

		//System.out.println("\t\t"+yr1+"\t"+yr2);

		double nameagr = labelagrdecay.computeAgr(t);
		double countryagr = countryagrdecay.computeAgr(t);
		double styleagr = styleagrdecay.computeAgr(t);
		double releaseagr = releaseagrdecay.computeAgr(t);
		double genresagr = genresagrdecay.computeAgr(t);
		double artistagr = artistagrdecay.computeAgr(t);
		double extraagr = extraagrdecay.computeAgr(t);
		double catnoagr = catnoagrdecay.computeAgr(t);
		double formatagr = formatagrdecay.computeAgr(t);


		double namedis = labeldisdecay.computeDis(t);
		double countrydis = countrydisdecay.computeDis(t);
		double styledis = styledisdecay.computeDis(t);
		double releasedis = releasedisdecay.computeDis(t);
		double genresdis = genresdisdecay.computeDis(t);
		double artistdis = artistdisdecay.computeDis(t);
		double extradis = extradisdecay.computeDis(t);
		double catnodis = catnodisdecay.computeDis(t);
		double formatdis = formatdisdecay.computeDis(t);

		//System.out.println("\t\t"+nameagr+"\t"+countryagr+"\t"+styleagr+"\t"+releaseagr+"\t"+genresagr);
		//System.out.println("\t\t"+namedis+"\t"+countrydis+"\t"+styledis+"\t"+releasedis+"\t"+genresdis);

		String name1 = a1.getLabel();
		String name2 = a2.getLabel();
		double namesim = attrSim.StrSim(name1, name2);
		double nameweight = 1;
		if (namesim >= th) {
			nameweight = 1 - nameagr;
		} else if (namesim < th2) {
			nameweight = 1 - namedis;
		} else {
			nameweight = namesim * (1 - nameagr) + (1 - namesim) * (1 - namedis);
		}

		//System.out.println("label \t"+name1+"\t"+name2+"\t"+namesim+"\t"+nameweight);

		String artist1 = a1.getStrartist();
		String artist2 = a2.getStrartist();
		double artistsim = attrSim.StrSim(artist1, artist2);
		double artistweight = 1;
		if (artistsim >= th) {
			artistweight = 1 - artistagr;
		} else if (artistsim < th2) {
			artistweight = 1 - artistdis;
		} else {
			artistweight = artistsim * (1 - artistagr) + (1 - artistsim) * (1 - artistdis);
		}

		//System.out.println("artist \t"+artist1+"\t"+artist2+"\t"+artistsim+"\t"+artistweight);

		String release1 = a1.getRelease();
		String release2 = a2.getRelease();
		double releasesim = attrSim.StrSim(release1, release2);
		double releaseweight = 1;
		if (releasesim >= th) {
			releaseweight = 1 - releaseagr;
		} else if (releasesim < th2) {
			releaseweight = 1 - releasedis;
		} else {
			releaseweight = releasesim * (1 - releaseagr) + (1 - releasesim) * (1 - releasedis);
		}

		//System.out.println("release \t"+release1+"\t"+release2+"\t"+releasesim+"\t"+releaseweight);

		String country1 = a1.getCountry();
		String country2 = a2.getCountry();
		double countrysim = attrSim.StrSim(country1, country2);
		double countryweight = 1;
		if (countrysim >= th) {
			countryweight = 1 - countryagr;
		} else if (countrysim < th2) {
			countryweight = 1 - countrydis;
		} else {
			countryweight = countrysim * (1 - countryagr) + (1 - countrysim) * (1 - countrydis);
		}
		//System.out.println("country \t"+country1+"\t"+country2+"\t"+countrysim+"\t"+countryweight);

		ArrayList genres1 = a1.getGenreslist();
		ArrayList genres2 = a2.getGenreslist();
		double genresim = attrSim.SetSim(genres1, genres2);
		double genresweight = 1;
		if (genresim >= th) {
			genresweight = 1 - genresagr;
		} else if (genresim < th2) {
			genresweight = 1 - genresdis;
		} else {
			genresweight = genresim * (1 - genresagr) + (1 - genresim) * (1 - genresdis);
		}

		//System.out.println("genres \t"+genres1+"\t"+genres2+"\t"+genresim+"\t"+genresweight);

		ArrayList styles1 = a1.getStyleslist();
		ArrayList styles2 = a2.getStyleslist();
		double stylesim = attrSim.SetSim(styles1, styles2);
		double styleweight = 1;
		if (stylesim >= th) {
			styleweight = 1 - styleagr;
		} else if (stylesim < th2) {
			styleweight = 1 - styledis;
		} else {
			styleweight = stylesim * (1 - styleagr) + (1 - stylesim) * (1 - styledis);
		}

		//System.out.println("style \t"+styles1+"\t"+styles2+"\t"+stylesim+"\t"+styleweight);

		String catno1 = a1.getCatno();
		String catno2 = a2.getCatno();
		double catnosim = attrSim.StrSim(catno1, catno2);
		double catnoweight = 1;
		if (catnosim >= th) {
			catnoweight = 1 - catnoagr;
		} else if (catnosim < th2) {
			catnoweight = 1 - catnodis;
		} else {
			catnoweight = catnosim * (1 - catnoagr) + (1 - catnosim) * (1 - catnodis);
		}
		//System.out.println("catno \t"+catno1+"\t"+catno2+"\t"+catnosim+"\t"+catnoweight);

		String extra1 = a1.getStrextrartist();
		String extra2 = a2.getStrextrartist();
		double extrasim = attrSim.StrSim(extra1, extra2);
		double extraweight = 1;
		if (extrasim >= th) {
			extraweight = 1 - extraagr;
		} else if (extrasim < th2) {
			extraweight = 1 - extradis;
		} else {
			extraweight = extrasim * (1 - extraagr) + (1 - extrasim) * (1 - extradis);
		}
		//System.out.println("extra_artist \t"+extra1+"\t"+extra2+"\t"+extrasim+"\t"+extraweight);

		String format1 = a1.getFormat() + "\t" + a1.getQty() + "\t" + a1.getFormat_description();
		String format2 = a2.getFormat() + "\t" + a2.getQty() + "\t" + a2.getFormat_description();
		double formatsim = attrSim.StrSim(format1, format2);
		double formatweight = 1;
		if (formatsim >= th) {
			formatweight = 1 - formatagr;
		} else if (formatsim < th2) {
			formatweight = 1 - formatdis;
		} else {
			formatweight = formatsim * (1 - formatagr) + (1 - formatsim) * (1 - formatdis);
		}
		//System.out.println("format \t"+format1+"\t"+format2+"\t"+formatsim+"\t"+formatweight);


		//System.out.println((namesim*nameweight + releasesim*releaseweight+countrysim*countryweight + genresim * genresweight + stylesim * styleweight)+" \t "+ (nameweight + releaseweight + countryweight + genresweight + styleweight));
		double sumweight = (nameweight + releaseweight + countryweight + genresweight + styleweight + artistweight + catnoweight + extraweight + formatweight + artistweight);

		if (sumweight == 0)
			score = 0;
		else
			score = (namesim * nameweight + artistsim * artistweight + releasesim * releaseweight + countrysim * countryweight + genresim * genresweight + stylesim * styleweight + catnosim * catnoweight + extrasim * extraweight + formatsim * formatweight)
					/ (nameweight + releaseweight + countryweight + genresweight + styleweight + artistweight + catnoweight + extraweight + formatweight);

		return score;
	}

	public double relabelAgreeSimilarity(ReleaseLabel a1, ReleaseLabel a2) {
		double score = 0;

		//System.out.println("compare \t"+a1.getGenres()+"\t"+a2.getGenres());

		int yr1 = a1.getDate();
		int yr2 = a2.getDate();
		int t = Math.abs(yr1 - yr2);

		//System.out.println("\t\t"+yr1+"\t"+yr2);

		double nameagr = labelagrdecay.computeAgr(t);
		double countryagr = countryagrdecay.computeAgr(t);
		double styleagr = styleagrdecay.computeAgr(t);
		double releaseagr = releaseagrdecay.computeAgr(t);
		double genresagr = genresagrdecay.computeAgr(t);
		double artistagr = artistagrdecay.computeAgr(t);
		double extraagr = extraagrdecay.computeAgr(t);
		double catnoagr = catnoagrdecay.computeAgr(t);
		double formatagr = formatagrdecay.computeAgr(t);


		double namedis = labeldisdecay.computeDis(t);
		double countrydis = countrydisdecay.computeDis(t);
		double styledis = styledisdecay.computeDis(t);
		double releasedis = releasedisdecay.computeDis(t);
		double genresdis = genresdisdecay.computeDis(t);
		double artistdis = artistdisdecay.computeDis(t);
		double extradis = extradisdecay.computeDis(t);
		double catnodis = catnodisdecay.computeDis(t);
		double formatdis = formatdisdecay.computeDis(t);

		//System.out.println("\t\t"+nameagr+"\t"+countryagr+"\t"+styleagr+"\t"+releaseagr+"\t"+genresagr);
		//System.out.println("\t\t"+namedis+"\t"+countrydis+"\t"+styledis+"\t"+releasedis+"\t"+genresdis);

		String name1 = a1.getLabel();
		String name2 = a2.getLabel();
		double namesim = attrSim.StrSim(name1, name2);
		double nameweight = 1;
		if (namesim >= th) {
			nameweight = 1 - nameagr;
		}


		//System.out.println("label \t"+name1+"\t"+name2+"\t"+namesim+"\t"+nameweight);

		String artist1 = a1.getStrartist();
		String artist2 = a2.getStrartist();
		double artistsim = attrSim.StrSim(artist1, artist2);
		double artistweight = 1;
		if (artistsim >= th) {
			artistweight = 1 - artistagr;
		}

		//System.out.println("artist \t"+artist1+"\t"+artist2+"\t"+artistsim+"\t"+artistweight);

		String release1 = a1.getRelease();
		String release2 = a2.getRelease();
		double releasesim = attrSim.StrSim(release1, release2);
		double releaseweight = 1;
		if (releasesim >= th) {
			releaseweight = 1 - releaseagr;
		}

		//System.out.println("release \t"+release1+"\t"+release2+"\t"+releasesim+"\t"+releaseweight);

		String country1 = a1.getCountry();
		String country2 = a2.getCountry();
		double countrysim = attrSim.StrSim(country1, country2);
		double countryweight = 1;
		if (countrysim >= th) {
			countryweight = 1 - countryagr;
		}

		//System.out.println("country \t"+country1+"\t"+country2+"\t"+countrysim+"\t"+countryweight);

		ArrayList genres1 = a1.getGenreslist();
		ArrayList genres2 = a2.getGenreslist();
		double genresim = attrSim.SetSim(genres1, genres2);
		double genresweight = 1;
		if (genresim >= th) {
			genresweight = 1 - genresagr;
		}

		//System.out.println("genres \t"+genres1+"\t"+genres2+"\t"+genresim+"\t"+genresweight);

		ArrayList styles1 = a1.getStyleslist();
		ArrayList styles2 = a2.getStyleslist();
		double stylesim = attrSim.SetSim(styles1, styles2);
		double styleweight = 1;
		if (stylesim >= th) {
			styleweight = 1 - styleagr;
		}

		//System.out.println("style \t"+styles1+"\t"+styles2+"\t"+stylesim+"\t"+styleweight);

		String catno1 = a1.getCatno();
		String catno2 = a2.getCatno();
		double catnosim = attrSim.StrSim(catno1, catno2);
		double catnoweight = 1;
		if (catnosim >= th) {
			catnoweight = 1 - catnoagr;
		}

		//System.out.println("catno \t"+catno1+"\t"+catno2+"\t"+catnosim+"\t"+catnoweight);

		String extra1 = a1.getStrextrartist();
		String extra2 = a2.getStrextrartist();
		double extrasim = attrSim.StrSim(extra1, extra2);
		double extraweight = 1;
		if (extrasim >= th) {
			extraweight = 1 - extraagr;
		}

		//System.out.println("extra_artist \t"+extra1+"\t"+extra2+"\t"+extrasim+"\t"+extraweight);

		String format1 = a1.getFormat() + "\t" + a1.getQty() + "\t" + a1.getFormat_description();
		String format2 = a2.getFormat() + "\t" + a2.getQty() + "\t" + a2.getFormat_description();
		double formatsim = attrSim.StrSim(format1, format2);
		double formatweight = 1;
		if (formatsim >= th) {
			formatweight = 1 - formatagr;
		}

		//System.out.println("format \t"+format1+"\t"+format2+"\t"+formatsim+"\t"+formatweight);


		//System.out.println((namesim*nameweight + releasesim*releaseweight+countrysim*countryweight + genresim * genresweight + stylesim * styleweight)+" \t "+ (nameweight + releaseweight + countryweight + genresweight + styleweight));
		double sumweight = (nameweight + releaseweight + countryweight + genresweight + styleweight + artistweight + catnoweight + extraweight + formatweight + artistweight);

		if (sumweight == 0)
			score = 0;
		else
			score = (namesim * nameweight + artistsim * artistweight + releasesim * releaseweight + countrysim * countryweight + genresim * genresweight + stylesim * styleweight + catnosim * catnoweight + extrasim * extraweight + formatsim * formatweight)
					/ (nameweight + releaseweight + countryweight + genresweight + styleweight + artistweight + catnoweight + extraweight + formatweight);

		return score;
	}

	public double decaySimilarity(ArtistData a1, ArtistData a2) {
		double score = 0;

		//System.out.println("compare \t"+a1.getGenres()+"\t"+a2.getGenres());

		int yr1 = Integer.parseInt(a1.getDate());
		int yr2 = Integer.parseInt(a2.getDate());
		int t = Math.abs(yr1 - yr2);

		//System.out.println("\t\t"+yr1+"\t"+yr2);

		double nameagr = nameagrdecay.computeAgr(t);
		double countryagr = countryagrdecay.computeAgr(t);
		double styleagr = styleagrdecay.computeAgr(t);
		double releaseagr = releaseagrdecay.computeAgr(t);
		double genresagr = genresagrdecay.computeAgr(t);

		double namedis = namedisdecay.computeDis(t);
		double countrydis = countrydisdecay.computeDis(t);
		double styledis = styledisdecay.computeDis(t);
		double releasedis = releasedisdecay.computeDis(t);
		double genresdis = genresdisdecay.computeDis(t);


		//System.out.println("\t\t"+nameagr+"\t"+countryagr+"\t"+styleagr+"\t"+releaseagr+"\t"+genresagr);
		//System.out.println("\t\t"+namedis+"\t"+countrydis+"\t"+styledis+"\t"+releasedis+"\t"+genresdis);

		String name1 = a1.getName();
		String name2 = a2.getName();
		double namesim = attrSim.StrSim(name1, name2);
		double nameweight = 1;
		if (namesim >= th) {
			nameweight = 1 - nameagr;
		} else if (namesim < th2) {
			nameweight = 1 - namedis;
		} else {
			nameweight = namesim * (1 - nameagr) + (1 - namesim) * (1 - namedis);
		}

		//System.out.println("name \t"+name1+"\t"+name2+"\t"+namesim+"\t"+nameweight);

		String release1 = a1.getRelease();
		String release2 = a2.getRelease();
		double releasesim = attrSim.StrSim(release1, release2);
		double releaseweight = 1;
		if (releasesim >= th) {
			releaseweight = 1 - releaseagr;
		} else if (releasesim < th2) {
			releaseweight = 1 - releasedis;
		} else {
			releaseweight = releasesim * (1 - releaseagr) + (1 - releasesim) * (1 - releasedis);
		}

		//System.out.println("release \t"+release1+"\t"+release2+"\t"+releasesim+"\t"+releaseweight);

		String country1 = a1.getCountry();
		String country2 = a2.getCountry();
		double countrysim = attrSim.StrSim(country1, country2);
		double countryweight = 1;
		if (countrysim >= th) {
			countryweight = 1 - countryagr;
		} else if (countrysim < th2) {
			countryweight = 1 - countrydis;
		} else {
			countryweight = countrysim * (1 - countryagr) + (1 - countrysim) * (1 - countrydis);
		}
		//System.out.println("country \t"+country1+"\t"+country2+"\t"+countrysim+"\t"+countryweight);

		ArrayList genres1 = a1.getGenres();
		ArrayList genres2 = a2.getGenres();
		double genresim = attrSim.SetSim(genres1, genres2);
		double genresweight = 1;
		if (genresim >= th) {
			genresweight = 1 - genresagr;
		} else if (genresim < th2) {
			genresweight = 1 - genresdis;
		} else {
			genresweight = genresim * (1 - genresagr) + (1 - genresim) * (1 - genresdis);
		}

		//System.out.println("genres \t"+genres1+"\t"+genres2+"\t"+genresim+"\t"+genresweight);

		ArrayList styles1 = a1.getStyle();
		ArrayList styles2 = a2.getStyle();
		double stylesim = attrSim.SetSim(styles1, styles2);
		double styleweight = 1;
		if (stylesim >= th) {
			styleweight = 1 - styleagr;
		} else if (stylesim < th2) {
			styleweight = 1 - styledis;
		} else {
			styleweight = stylesim * (1 - styleagr) + (1 - stylesim) * (1 - styledis);
		}

		double sumweight = (nameweight + releaseweight + countryweight + genresweight + styleweight);
		//System.out.println("style \t"+styles1+"\t"+styles2+"\t"+stylesim+"\t"+styleweight);

		//System.out.println((namesim*nameweight + releasesim*releaseweight+countrysim*countryweight + genresim * genresweight + stylesim * styleweight)+" \t "+ (nameweight + releaseweight + countryweight + genresweight + styleweight));

		if (sumweight == 0)
			score = 0;
		else
			score = (namesim * nameweight + releasesim * releaseweight + countrysim * countryweight + genresim * genresweight + stylesim * styleweight) / (nameweight + releaseweight + countryweight + genresweight + styleweight);

		return score;
	}

	public double agreeSimilarity(ArtistData a1, ArtistData a2) {
		double score = 0;

		int yr1 = Integer.parseInt(a1.getDate());
		int yr2 = Integer.parseInt(a2.getDate());
		int t = Math.abs(yr1 - yr2);

		//System.out.println("\t\t"+yr1+"\t"+yr2+"\t"+t);

		double nameagr = nameagrdecay.computeAgr(t);
		double countryagr = countryagrdecay.computeAgr(t);
		double styleagr = styleagrdecay.computeAgr(t);
		double releaseagr = releaseagrdecay.computeAgr(t);
		double genresagr = genresagrdecay.computeAgr(t);
		//System.out.println("\t\t"+nameagr+"\t"+countryagr+"\t"+styleagr+"\t"+releaseagr+"\t"+genresagr);

		String name1 = a1.getName();
		String name2 = a2.getName();
		double namesim = attrSim.StrSim(name1, name2);
		double nameweight = 1;
		if (namesim >= th)
			nameweight = 1 - nameagr;
		//System.out.println(name1+"\t"+name2+"\t"+namesim+"\t"+nameweight);

		String release1 = a1.getRelease();
		String release2 = a2.getRelease();
		double releasesim = attrSim.StrSim(release1, release2);
		double releaseweight = 1;
		if (releasesim >= th)
			releaseweight = 1 - releaseagr;
		//System.out.println(release1+"\t"+release2+"\t"+releasesim+"\t"+releaseweight);

		String country1 = a1.getCountry();
		String country2 = a2.getCountry();
		double countrysim = attrSim.StrSim(country1, country2);
		double countryweight = 1;
		if (countrysim >= th)
			countryweight = 1 - countryagr;
		//System.out.println(country1+"\t"+country2+"\t"+countrysim+"\t"+countryweight);

		ArrayList genres1 = a1.getGenres();
		ArrayList genres2 = a2.getGenres();
		double genresim = attrSim.SetSim(genres1, genres2);
		double genresweight = 1;
		if (genresim >= th)
			genresweight = 1 - genresagr;
		//System.out.println(genres1+"\t"+genres2+"\t"+genresim+"\t"+genresweight);

		ArrayList styles1 = a1.getStyle();
		ArrayList styles2 = a2.getStyle();
		double stylesim = attrSim.SetSim(styles1, styles2);
		double styleweight = 1;
		if (stylesim >= th)
			styleweight = 1 - styleagr;
		//System.out.println(styles1+"\t"+styles2+"\t"+stylesim+"\t"+styleweight);

		double sumweight = (nameweight + releaseweight + countryweight + genresweight + styleweight);

		if (sumweight == 0)
			score = 0;
		else
			score = (namesim * nameweight + releasesim * releaseweight + countrysim * countryweight + genresim * genresweight + stylesim * styleweight) / (nameweight + releaseweight + countryweight + genresweight + styleweight);

		return score;
	}

	public double labelAgreeSimilarity(LabelData a1, LabelData a2) {
		double score = 0;

		int yr1 = Integer.parseInt(a1.getDate());
		int yr2 = Integer.parseInt(a2.getDate());
		int t = Math.abs(yr1 - yr2);

		//System.out.println("\t\t"+yr1+"\t"+yr2+"\t"+t);

		double nameagr = nameagrdecay.computeAgr(t);
		double countryagr = countryagrdecay.computeAgr(t);
		double styleagr = styleagrdecay.computeAgr(t);
		double releaseagr = releaseagrdecay.computeAgr(t);
		double genresagr = genresagrdecay.computeAgr(t);
		double artistagr = artistagrdecay.computeAgr(t);
		//System.out.println("\t\t"+nameagr+"\t"+countryagr+"\t"+styleagr+"\t"+releaseagr+"\t"+genresagr);

		String name1 = a1.getName();
		String name2 = a2.getName();
		double namesim = attrSim.StrSim(name1, name2);
		double nameweight = 1;
		if (namesim >= th)
			nameweight = 1 - nameagr;
		//System.out.println(name1+"\t"+name2+"\t"+namesim+"\t"+nameweight);


		String artist1 = a1.getArtist();
		String artist2 = a2.getArtist();
		double artistsim = attrSim.StrSim(artist1, artist2);
		double artistweight = 1;
		if (artistsim >= th) {
			artistweight = 1 - artistagr;
		}


		String release1 = a1.getRelease();
		String release2 = a2.getRelease();
		double releasesim = attrSim.StrSim(release1, release2);
		double releaseweight = 1;
		if (releasesim >= th)
			releaseweight = 1 - releaseagr;
		//System.out.println(release1+"\t"+release2+"\t"+releasesim+"\t"+releaseweight);

		String country1 = a1.getCountry();
		String country2 = a2.getCountry();
		double countrysim = attrSim.StrSim(country1, country2);
		double countryweight = 1;
		if (countrysim >= th)
			countryweight = 1 - countryagr;
		//System.out.println(country1+"\t"+country2+"\t"+countrysim+"\t"+countryweight);

		ArrayList genres1 = a1.getGenres();
		ArrayList genres2 = a2.getGenres();
		double genresim = attrSim.SetSim(genres1, genres2);
		double genresweight = 1;
		if (genresim >= th)
			genresweight = 1 - genresagr;
		//System.out.println(genres1+"\t"+genres2+"\t"+genresim+"\t"+genresweight);

		ArrayList styles1 = a1.getStyle();
		ArrayList styles2 = a2.getStyle();
		double stylesim = attrSim.SetSim(styles1, styles2);
		double styleweight = 1;
		if (stylesim >= th)
			styleweight = 1 - styleagr;
		//System.out.println(styles1+"\t"+styles2+"\t"+stylesim+"\t"+styleweight);

		double sumweight = (nameweight + releaseweight + countryweight + genresweight + styleweight + artistweight);

		if (sumweight == 0)
			score = 0;
		else
			score = (namesim * nameweight + releasesim * releaseweight + countrysim * countryweight + genresim * genresweight + stylesim * styleweight + artistsim * artistweight) / sumweight;

		return score;
	}


	public double recordSimilarity(ArtistData a1, ArtistData a2) {
		double score = 0;
		double nameTh = 1;
		double trackTh = 1 - nameTh;
		
		/*
		String name1 = a1.getName();
		ArrayList tracks1 = a1.getTracks();
		
		String name2 = a2.getName();
		ArrayList tracks2 = a2.getTracks();
		
		double nameScore = AttributeSimilarity.StrSim(name1, name2);
		double trackScore = 0;
		if(!tracks2.isEmpty()&&!tracks1.isEmpty())
		trackScore = AttributeSimilarity.SetSim(tracks2, tracks1);
		
		score = nameTh*nameScore + trackTh*trackScore;
		*/

		String name1 = a1.getReal_name();

		String name2 = a2.getReal_name();

		double nameScore = 0;//AttributeSimilarity.StrSim(name1, name2);
		double trackScore = 0;
		score = nameTh * nameScore + trackTh * trackScore;

		//System.out.println("record sim: \t"+a1.getId()+" vs "+a2.getId()+": \t"+nameScore+"\t"+trackScore+"\t"+score);
		return score;
	}

}
