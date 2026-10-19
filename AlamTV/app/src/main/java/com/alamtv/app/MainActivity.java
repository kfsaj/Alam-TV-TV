package com.alamtv.app;

import android.app.*;
import android.os.*;
import android.content.*;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.view.*;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import org.json.*;
import java.net.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class MainActivity extends Activity {
 static class Item {String name, group, url; boolean external; Item(String n,String g,String u,boolean e){name=n;group=g;url=u;external=e;}}
 final ArrayList<Item> items=new ArrayList<>(); final HashSet<String> favorites=new HashSet<>();
 LinearLayout list, tabs; EditText search; String filter="الكل"; int bg=0xff091426;
 @Override public void onCreate(Bundle b){super.onCreate(b);getWindow().setStatusBarColor(bg);getWindow().setNavigationBarColor(bg);
   favorites.addAll(getPreferences(0).getStringSet("favorites",new HashSet<>()));
   add("NASA+ | ناسا","قنوات رسمية","https://plus.nasa.gov/",true);
   add("NASA Live | بث مباشر","قنوات رسمية","https://www.nasa.gov/live/",true);
   add("Al Jazeera Live | الجزيرة","قنوات رسمية","https://www.aljazeera.net/live/",true);
   add("France 24 Arabic | فرانس 24","قنوات رسمية","https://www.france24.com/ar/live",true);
   add("DW Arabic | دويتشه فيله","قنوات رسمية","https://www.dw.com/ar/",true);
   add("Internet Archive | أفلام متاحة","أفلام مجانية","https://archive.org/details/movies",true);
   add("Plex | أفلام وقنوات مجانية","أفلام مجانية","https://watch.plex.tv/",true);
   add("Big Buck Bunny | فيلم تجريبي مفتوح","فيديو تجريبي","https://storage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",false);
   build();
 }
 void add(String n,String g,String u,boolean ext){items.add(new Item(n,g,u,ext));}
 GradientDrawable shape(int color,int radius){GradientDrawable d=new GradientDrawable();d.setColor(color);d.setCornerRadius(radius);return d;}
 TextView text(String s,int size, int color){TextView t=new TextView(this);t.setText(s);t.setTextSize(size);t.setTextColor(color);t.setGravity(Gravity.CENTER_VERTICAL);return t;}
 Button button(String label){Button b=new Button(this); b.setText(label);b.setTextSize(14);b.setTextColor(Color.WHITE);b.setAllCaps(false);b.setBackground(shape(0xff163253,15));b.setFocusable(true);b.setPadding(14,6,14,6);return b;}
 void build(){ScrollView root=new ScrollView(this);root.setFillViewport(true);root.setBackgroundColor(bg);setContentView(root);
   LinearLayout outer=new LinearLayout(this);outer.setOrientation(1);outer.setPadding(22,20,22,35);outer.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);root.addView(outer);
   TextView title=text("عالم TV  ◉",30,0xff6edcf4);title.setTypeface(null,Typeface.BOLD);outer.addView(title);
   TextView desc=text("قنوات رسمية • أفلام مجانية • قوائمك الخاصة | بدون حساب أو اشتراك",14,0xffa7b7cb);outer.addView(desc);
   search=new EditText(this);search.setSingleLine(true);search.setHint("ابحث عن قناة أو فيلم..."); search.setTextColor(Color.WHITE);search.setHintTextColor(0xffa6b4c6);search.setBackground(shape(0xff172943,15));search.setPadding(18,12,18,12);
   LinearLayout.LayoutParams sp=new LinearLayout.LayoutParams(-1,-2);sp.topMargin=18;outer.addView(search,sp);
   search.addTextChangedListener(new android.text.TextWatcher(){public void beforeTextChanged(CharSequence s,int st,int c,int a){}public void onTextChanged(CharSequence s,int st,int before,int count){render();}public void afterTextChanged(android.text.Editable e){}});
   LinearLayout actions=new LinearLayout(this);actions.setOrientation(LinearLayout.HORIZONTAL);actions.setPadding(0,14,0,10);outer.addView(actions);
   Button importBtn=button("+ استيراد M3U");actions.addView(importBtn); importBtn.setOnClickListener(v->promptImport());
   Button customBtn=button("+ رابط بث مباشر");LinearLayout.LayoutParams cp=new LinearLayout.LayoutParams(-2,-2);cp.leftMargin=10;actions.addView(customBtn,cp);customBtn.setOnClickListener(v->promptStream());
   HorizontalScrollView scroll=new HorizontalScrollView(this);scroll.setHorizontalScrollBarEnabled(false);outer.addView(scroll);
   tabs=new LinearLayout(this);tabs.setOrientation(LinearLayout.HORIZONTAL);scroll.addView(tabs);tabs();
   list=new LinearLayout(this);list.setOrientation(1);outer.addView(list);render();
   TextView note=text("المحتوى يتغير حسب المنطقة وتوافر المصدر. الروابط الرسمية تُفتح في الموقع الأصلي. أضف فقط قوائم بث مخوّلًا باستخدامها.",12,0xffa7b7cb);note.setPadding(0,16,0,8);outer.addView(note);
 }
 void tabs(){tabs.removeAllViews();ArrayList<String> names=new ArrayList<>(Arrays.asList("الكل","قنوات رسمية","أفلام مجانية","فيديو تجريبي","قوائمي","المفضلة"));
 for(String f:names){Button b=button(f);if(f.equals(filter))b.setBackground(shape(0xff136b87,16));LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-2,-2);lp.leftMargin=8;tabs.addView(b,lp);b.setOnClickListener(v->{filter=f;tabs();render();});}}
 void render(){if(list==null)return;list.removeAllViews();String q=search==null?"":search.getText().toString().trim().toLowerCase(Locale.ROOT);int n=0;
 for(Item item:items){if(!filter.equals("الكل")&&!filter.equals("المفضلة")&&!(filter.equals("قوائمي")&&item.group.startsWith("قوائمي"))&&!filter.equals(item.group))continue;
   if(filter.equals("المفضلة")&&!favorites.contains(item.url))continue;
   if(!item.name.toLowerCase(Locale.ROOT).contains(q)&&!item.group.toLowerCase(Locale.ROOT).contains(q))continue;n++;
   LinearLayout card=new LinearLayout(this);card.setOrientation(1);card.setPadding(18,12,18,12);card.setBackground(shape(0xff13243b,18));LinearLayout.LayoutParams cl=new LinearLayout.LayoutParams(-1,-2);cl.topMargin=12;list.addView(card,cl);
   TextView name=text(item.name,19,Color.WHITE);name.setTypeface(null,Typeface.BOLD);card.addView(name);
   TextView group=text(item.group+(item.external?" • موقع رسمي/خارجي":" • تشغيل داخل التطبيق"),12,0xff9dafc5);card.addView(group);
   LinearLayout row=new LinearLayout(this);row.setOrientation(LinearLayout.HORIZONTAL);card.addView(row);
   Button play=button(item.external?"فتح المصدر ↗":"▶ تشغيل");row.addView(play);play.setOnClickListener(v->open(item));
   Button fav=button(favorites.contains(item.url)?"★ مفضلة":"☆ أضف للمفضلة");LinearLayout.LayoutParams fp=new LinearLayout.LayoutParams(-2,-2);fp.leftMargin=9;row.addView(fav,fp);
   fav.setOnClickListener(v->{if(!favorites.remove(item.url))favorites.add(item.url);getPreferences(0).edit().putStringSet("favorites",new HashSet<>(favorites)).apply();render();});
 }
 if(n==0){TextView empty=text("لا توجد نتائج. أضف قائمة M3U أو غيّر البحث.",16,0xffb6c7dd);empty.setPadding(6,28,6,10);list.addView(empty);}}
 void open(Item it){try {Intent i;if(it.external){i=new Intent(Intent.ACTION_VIEW,Uri.parse(it.url));}else{i=new Intent(this,PlayerActivity.class);i.putExtra("url",it.url);}startActivity(i);}catch(Exception e){toast("لا يوجد تطبيق متاح لفتح الرابط");}}
 void promptStream(){EditText e=new EditText(this);e.setHint("https://example.com/live.m3u8");e.setSingleLine(true);
 new AlertDialog.Builder(this).setTitle("إضافة بث تملكه أو لديك إذن به").setView(e).setNegativeButton("إلغاء",null).setPositiveButton("إضافة",(d,w)->{String u=e.getText().toString().trim();if(!u.startsWith("https://")){toast("أدخل رابط HTTPS صالحًا");return;}add("بث مخصص "+(items.size()+1),"قوائمي",u,false);render();}).show();}
 void promptImport(){EditText e=new EditText(this);e.setSingleLine(true);e.setHint("https://example.com/playlist.m3u");
 new AlertDialog.Builder(this).setTitle("استيراد قائمة M3U مصرح بها (HTTPS)").setMessage("يتم تحميل قائمة قنواتك من رابط مباشر. الحد الأقصى 2MB و500 قناة.")
 .setView(e).setNegativeButton("إلغاء",null).setPositiveButton("استيراد",(d,w)->{String u=e.getText().toString().trim();if(!u.startsWith("https://")){toast("رابط HTTPS مطلوب");return;}loadPlaylist(u);}).show();}
 void loadPlaylist(String link){new Thread(()->{try{
 URL url=new URL(link);URLConnection conn=url.openConnection();if(!(conn instanceof HttpURLConnection))throw new IOException("HTTPS فقط");
 HttpURLConnection http=(HttpURLConnection)conn;http.setInstanceFollowRedirects(false);http.setConnectTimeout(10000);http.setReadTimeout(12000);
 if(http.getResponseCode()!=200)throw new IOException("HTTP "+http.getResponseCode());
 ByteArrayOutputStream bout=new ByteArrayOutputStream();try(InputStream in=http.getInputStream()){byte[] buf=new byte[8192];int r;while((r=in.read(buf))!=-1){bout.write(buf,0,r);if(bout.size()>2_000_000)throw new IOException("قائمة كبيرة جدًا");}}
 String content=bout.toString("UTF-8");if(!content.trim().startsWith("#EXTM3U"))throw new IOException("ليست قائمة M3U");
 ArrayList<Item> parsed=new ArrayList<>();String name="",group="قوائمي";
 for(String raw:content.split("\r?\n")){String line=raw.trim();if(line.startsWith("#EXTINF")){int k=line.lastIndexOf(',');name=k>=0?line.substring(k+1).trim():"قناة";java.util.regex.Matcher m=java.util.regex.Pattern.compile("group-title=\"([^\"]+)\"").matcher(line);group=m.find()?"قوائمي • "+m.group(1):"قوائمي";}
 else if(line.startsWith("https://")&&!name.isEmpty()){parsed.add(new Item(name,group,line,false));name="";if(parsed.size()>=500)break;}}
 if(parsed.isEmpty())throw new IOException("لا توجد روابط HTTPS صالحة");
 runOnUiThread(()->{items.addAll(parsed);filter="الكل";tabs();render();toast("أُضيفت "+parsed.size()+" قناة");});
 }catch(Exception ex){runOnUiThread(()->toast("فشل الاستيراد: "+ex.getMessage()));}}).start();}
 void toast(String s){Toast.makeText(this,s,Toast.LENGTH_LONG).show();}
}
