package com.reviewscraper.games;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class TestScraperApplication {
	   public static void main(String[] args) {
	   
		   String url = "https://www.google.com/search?q=last+of+us+review+ign";
		   
		      Document d = null;
			try {
				d = Jsoup.connect(url).get();
			} catch (IOException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
			
				Element link =d.select("h3.r").first();
				Element link2 = link.select("a").first();
				String linkHref = link2.attr("href");
				System.out.println(linkHref);
			try {
				d = Jsoup.connect(linkHref).get();
			} catch (IOException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
			String score =d.select("span.score").first().text();
			System.out.println(score);
			//Document document2 = Jsoup.parse(scraped);
		     // System.out.println(document2.select("span").first().text());
	   }
}