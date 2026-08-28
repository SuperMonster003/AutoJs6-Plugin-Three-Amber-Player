<!--suppress HtmlDeprecatedAttribute, HttpUrlsUsage -->

<div align="center">
  <p>
    <img src="https://github.com/SuperMonster003/AutoJs6-Plugin-Video-Player/blob/master/app/src/main/res/mipmap/ic_launcher.png?raw=true" alt="video-player-ic-launcher" border="0" width="128" />
  </p>

  <p>ملحق مدير الملفات. تشغيل ملفات الفيديو مباشرة</p>

  <p>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-Video-Player/releases"><img alt="GitHub release (latest by date)" src="https://img.shields.io/github/v/release/SuperMonster003/AutoJs6-Plugin-Video-Player?label=Release"/></a>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-Video-Player/issues"><img alt="GitHub closed issues" src="https://img.shields.io/github/issues/SuperMonster003/AutoJs6-Plugin-Video-Player?color=A24232&label=Issues"/></a>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-Video-Player/blob/master/LICENSE"><img alt="GitHub License" src="https://img.shields.io/github/license/SuperMonster003/AutoJs6-Plugin-Video-Player?color=534BAE&label=License"/></a>
  </p>
</div>

******

### اللغات (Languages)

******

يدعم ملف README.md الحالي اللغات التالية:

- [简体中文 [zh-Hans]](https://github.com/SuperMonster003/AutoJs6-Plugin-Video-Player/blob/master/.readme/README-zh-Hans.md)
- [繁體中文 (香港) [zh-Hant-HK]](https://github.com/SuperMonster003/AutoJs6-Plugin-Video-Player/blob/master/.readme/README-zh-Hant-HK.md)
- [繁體中文 (台灣) [zh-Hant-TW]](https://github.com/SuperMonster003/AutoJs6-Plugin-Video-Player/blob/master/.readme/README-zh-Hant-TW.md)
- [English [en]](https://github.com/SuperMonster003/AutoJs6-Plugin-Video-Player/blob/master/.readme/README-en.md)
- [Français [fr]](https://github.com/SuperMonster003/AutoJs6-Plugin-Video-Player/blob/master/.readme/README-fr.md)
- [Español [es]](https://github.com/SuperMonster003/AutoJs6-Plugin-Video-Player/blob/master/.readme/README-es.md)
- [日本語 [ja]](https://github.com/SuperMonster003/AutoJs6-Plugin-Video-Player/blob/master/.readme/README-ja.md)
- [한국어 [ko]](https://github.com/SuperMonster003/AutoJs6-Plugin-Video-Player/blob/master/.readme/README-ko.md)
- [Русский [ru]](https://github.com/SuperMonster003/AutoJs6-Plugin-Video-Player/blob/master/.readme/README-ru.md)
- العربية [ar] # الحالي

******

### مقدمة

******

يشغل Video Player محتوى الفيديو الذي يقدمه مدير الملفات أو تطبيق Android آخر عبر وصول مؤقت للقراءة فقط إلى content URI. يستخدم AndroidX Media3 ExoPlayer ولا يعدل فيديو المصدر مطلقا.

******

### الميزات

******

- توافق خفيف لـ XVID داخل MKV عبر وحدة فك ترميز MPEG-4 Part 2 المدمجة في الجهاز، مع استرداد واضح بمشغل خارجي عندما لا تتوفر وحدة فك ترميز النظام أو تفشل.
- يسجل إجراء Explorer رئيسيا للقراءة فقط عبر الإصدار 12 من بروتوكول `org.autojs.plugin.EXPLORER_ACTION` المشترك، مع إمكانات اختيارية ومحدودة للملفات الشقيقة وتقدم التشغيل.
- يستخدم AndroidX Media3 ExoPlayer وPlayerView للتشغيل التلقائي وعناصر التحكم القياسية وتركيز الصوت ومعالجة تحول خرج الصوت إلى حالة noisy والتكامل مع برامج ترميز الجهاز.
- يستعيد موضع التشغيل وحالة التشغيل أو الإيقاف المؤقت بعد إعادة الإنشاء ويبقي الشاشة قيد التشغيل فقط أثناء تشغيل الفيديو فعليا.
- يوفر مدخلا مستقلا ومصدرا للإجراء `android.intent.action.VIEW` من أجل content URI للقراءة فقط بنوع MIME يطابق `video/*`.
- يوفر إجراء فتح باستخدام تطبيق آخر بشكل آمن بعد فشل التشغيل عبر إعادة بناء Intent عرض للقراءة فقط واستبعاد هذا الملحق من قائمة التطبيقات المرشحة.
- يوفر تشغيلا غامرا بملء الشاشة مع إيماءات للسطوع ومستوى الصوت والتقديم, وانتقالات بالنقر المزدوج, وسرعة مؤقتة بالضغط المطول, وقفل عناصر التحكم.
- يوفر سرعات تشغيل من 0.25× إلى 3× مع تبديل وضع العرض واتجاه الشاشة بلمسة واحدة.
- يتذكر محليا موضع الاستئناف مفهرسا بملخصات content URI ويمسحه بعد انتهاء الفيديو.
- يتيح اختيار المسارات الصوتية والترجمة المضمنة مع إيقاف الترجمة افتراضيا وتحديد المسارات غير المدعومة بوضوح.
- يوفر تكرار الفيديو الحالي ولوحة بيانات وصفية مفصلة واتجاها حسب نسبة العرض ووضع صورة داخل صورة على API 26+ مع تشغيل/إيقاف مؤقت عن بعد.
- يدمج MediaSession لعناصر تحكم سماعات الرأس وBluetooth والنظام, مع إشعار وسائط يعرض العنوان وتقدم التشغيل.
- مؤقت نوم لمدة 15/30/45/60 دقيقة أو حتى نهاية الفيديو, مع حفظ حساسية الإيماءات ومدة التقديم بالنقر المزدوج 5/10/30 ثانية.
- تكبير بإصبعين من 0.25× إلى 4× مع إعادة الضبط بالنقر المزدوج, ووقت مستهدف وصور مصغرة اختيارية في الذاكرة أثناء التقديم.
- لقطات للإطار الحالي بلا أذونات على Android 10+, تحفظ كملفات PNG منفصلة عبر MediaStore من دون تعديل الفيديو المصدر.
- قوائم انتظار فيديو مرتبة ترتيبا طبيعيا من المجلد نفسه عبر Explorer Action v12، مع السابق / التالي والتسلسل والعشوائي وتكرار عنصر واحد والتشغيل التلقائي للعنصر التالي.
- اكتشاف ملفات الترجمة الجانبية المطابقة .srt و.ass، بما في ذلك لاحقات اللغة، مع إبقائها متوقفة افتراضيا وعدم تحميلها إلا بعد اختيار صريح.
- سجل استئناف اختياري يديره المضيف، متوقف افتراضيا ويمكن تعطيله أو مسحه من إعدادات AutoJs6، من دون علامة مشاهدة في قائمة الملفات.

******

### التكامل مع المضيف

******

يستخدم المضيف هذا الملحق لمسار فتح الفيديو الرئيسي وإجراء التشغيل.

بعد تثبيت الملحق وتمكينه وتوثيقه والتأكد من توافقه يؤدي فتح أي ملف يصنفه المضيف كفيديو إلى تشغيل الإجراء `play-video` بوصفه عارض Explorer الرئيسي.

إذا كان الملحق مفقودا أو معطلا أو غير مخول أو غير متاح أو غير متوافق أو تعذر تشغيله يعرض المضيف المتوافق إرشادات الاسترداد. لا تفتح قائمة تطبيقات النظام إلا بعد أن يختار المستخدم صراحة فتح باستخدام تطبيقات أخرى.

يطابق هذا الملحق ملفات الفيديو فقط. يظل تشغيل الصوت وعرض الصور قدرات مستقلة لملحقات أخرى ولا يتم تضمينها في ملف APK هذا.

******

### التنسيقات المدعومة

******

يقبل إجراء Explorer الرئيسي النوع `video/*` لكل أنواع الفيديو التي يتعرف عليها المضيف ويحتفظ بمطابقات الامتدادات الدقيقة الـ 23 التالية للتوافق مع المضيفات القديمة:

```text
MP4, MPEG4, MPG4, AVI, MKV, MOV, FLV, WEBM, M4V, 3GP, MPEG, 3G2, 3GP2, 3GPP, F4V, M2T, M2TS, MTS, TS, MPG, MPE, VOB, QT
```

******

### واجهة الملحق

******

يكتشف المضيف الملحق وينفذه بالمعرفات التالية:

```text
service action: org.autojs.plugin.EXPLORER_ACTION
execute action: org.autojs.plugin.EXPLORER_ACTION_EXECUTE
plugin id: video-player
engine: explorer-action
variant: default
Explorer action id: play-video
Explorer placement: primary
access mode: read-only
Explorer MIME types: video/*
external view action: android.intent.action.VIEW
external MIME type: video/*
required host build: 5276
```

يوفر الإصدار 1.4 إجراء Explorer رئيسيا للقراءة فقط عبر البروتوكول v12. يمكن للمضيف المتوافق إرفاق إمكانات مباشرة محدودة بالملفات الشقيقة وتقدم التشغيل لكل طلب؛ وتحتفظ المضيفات التي لا تدعم هذه الإضافات الاختيارية بتشغيل ملف واحد. يظل المدخل الخارجي المستقل مقتصرا على content URI للقراءة فقط مع نوع MIME فرعي صالح من `video/*`.

تتطلب ميزات التعاون الكامل AutoJs6 6.8.0 build 5276 أو أحدث وExplorer Action v12؛ ولن يزداد هذا المتطلب مع إمكانات الملحق اللاحقة.

******

### الأمان

******

لا يطلب الملحق إذن التخزين أو INTERNET. تتحقق حدود Explorer المحمية بالتوقيع من غلاف البروتوكول v12 كاملا ومن هدف واحد محدد وعلاقة الأصل وClipData والبيانات الوصفية وبناء المضيف ومنح القراءة فقط. ترتبط Host Session الاختيارية بواسطة المضيف بمعرف UID للملحق، ولا تسمح إلا بسرد المجلد الأب المباشر للملف المحدد وفتح الملف المحدد أو ملف شقيق مباشر قابل للقراءة. يتحقق المشغل الخاص من قائمة انتظار معتمة محدودة ولا يتلقى مسار نظام ملفات. تظل حدود ACTION_VIEW العامة مستقلة ومقتصرة على ملف واحد.

******

### حدود الأمان

******

- يبدأ Explorer من content URI واحد محدد بالضبط؛ ويمكن لـ Host Session الاختيارية كشف الملفات الشقيقة المباشرة القابلة للقراءة فقط، ولا تسمح أبدا بوصول متكرر إلى المجلدات.
- يتطلب تنفيذ Explorer إذن المستوى signature المسمى `org.autojs.permission.PLUGIN`.
- يتم دائما رفض منح الكتابة والمنح الدائمة. لا تتم إعادة توجيه وصول prefix المستخدم للتحقق من URI الأصل إلى المشغل.
- ترفض حدود ACTION_VIEW العامة منح الكتابة والمنح الدائمة ومنح prefix.
- يتم حل التطبيقات المرشحة الخارجية أولا وتصفيتها للاحتفاظ بالحزم الأخرى وتشغيلها باستخدام Intent جديد للقراءة فقط.
- سجل تقدم التشغيل لدى المضيف متوقف افتراضيا، ولا يخزن بعد الاشتراك الصريح إلا ملخصات المسارات القياسية والقيم الزمنية، ويمكن تعطيله أو مسحه من إعدادات AutoJs6.
- لا يضمن التصنيف كفيديو أو وجود امتداد قديم مدرج دعم فك الترميز على كل جهاز. يحدد Media3 وبرامج ترميز النظام المثبتة دعم التشغيل الفعلي.

******

### سجل الإصدارات

******

# v1.4.0

###### 2026/08/28

* `ميزة` إنشاء قوائم فيديو مرتبة طبيعيا من المجلد نفسه عبر Host Session في Explorer Action v12 مقيدة بالطلب ومرتبطة بمعرف UID، مع السابق / التالي والتسلسل والعشوائي وتكرار عنصر واحد والتشغيل التلقائي للعنصر التالي
* `ميزة` اكتشاف ملفات الترجمة الخارجية .srt و.ass المطابقة، بما فيها صيغ لاحقة اللغة، مع إبقائها معطلة افتراضيا وعدم تحميلها إلا بعد اختيار صريح
* `ميزة` إضافة سجل استئناف اختياري يملكه المضيف: التسجيل معطل افتراضيا ويمكن تعطيله أو مسحه من إعدادات AutoJs6 ولا يضيف علامة مشاهدة إلى قائمة الملفات
* `إصلاح` كانت طلبات Explorer التي يصنفها المضيف كفيديو ترفض إذا لم يكن امتدادها ضمن قائمة السماح القديمة المكونة من 23 عنصرا؛ وأصبحت طلبات `video/*` الموثوقة تقبل بصورة موحدة
* `تحسين` حصر الوصول في الملف المحدد والملفات الشقيقة المباشرة القابلة للقراءة، من دون اجتياز متكرر أو كتابة أو منح دائم أو مسارات نصية صريحة داخل الملحق
* `تحسين` تقييد القوائم المتسلسلة إلى 128 فيديو و8 ملفات ترجمة لكل فيديو و128 ارتباط ترجمة إجمالا مع الاحتفاظ دائما بالعنصر المحدد
* `تحسين` الإبقاء على التوافق مع AutoJs6 6.8.0 build 5276 وExplorer Action v12؛ وتحتفظ المضيفات التي لا تدعم الامتدادات الاختيارية بتشغيل ملف واحد بأمان
* `تبعية` ترقية Explorer Action API المضمنة من البروتوكول v2 إلى امتداد جلسة الوسائط v12 المتوافق مع الإصدارات السابقة

# v1.3.1

###### 2026/08/27

* `إصلاح` إضافة طبقة توافق خفيفة لـ XVID داخل MKV تمرر مسارات VFW/FourCC XVID المتحقق منها إلى وحدة فك ترميز MPEG-4 Part 2 المدمجة في الجهاز دون تحويل الترميز أو تعديل الملف المصدر
* `إصلاح` إيقاف التشغيل الصوتي فقط عند عدم وجود وحدة فك ترميز نظام متوافقة أو عند فشل فك الترميز، مع توضيح مخصص وخيار الفتح باستخدام تطبيق آخر

# v1.3.0

###### 2026/08/27

* `ميزة` مؤقت النوم: إيقاف مؤقت بعد 15 أو 30 أو 45 أو 60 دقيقة أو عند نهاية الفيديو الحالي مع معالجة التعارض مع التكرار
* `ميزة` التفاعل مع الصورة: تكبير بإصبعين من 0.25× إلى 4× وإعادة الضبط بالنقر المزدوج والتنسيق مع أوضاع العرض الحالية
* `ميزة` معاينة التقديم: وقت مستهدف وصور مصغرة اختيارية في الذاكرة مع الرجوع بصمت إلى الوقت فقط عند الفشل
* `ميزة` لقطات للإطار الحالي على Android 10+ تحفظ كملفات PNG منفصلة عبر MediaStore بلا إذن تخزين أو تعديل للفيديو المصدر
* `ميزة` إعدادات محفوظة لحساسية إيماءات منخفضة أو عادية أو مرتفعة وتقديم بالنقر المزدوج 5 أو 10 أو 30 ثانية
* `تحسين` تستخدم مواعيد مؤقت النوم الوقت المنقضي وتبقى بعد إعادة إنشاء حالة صفحة التشغيل من دون التأثر بتغيير الساعة
* `تحسين` يدمج استخراج الصور المصغرة طلبات السحب السريعة في عامل واحد ويحرر كل صورة مؤقتة أصبحت قديمة

##### لمزيد من الإصدارات

* [CHANGELOG-ar.md](https://github.com/SuperMonster003/AutoJs6-Plugin-Video-Player/blob/master/app/src/main/assets/doc/CHANGELOG-ar.md)

******

### البناء

******

```powershell
.\gradlew.bat :app:assembleDebug
```

بناء Release:

```powershell
.\gradlew.bat :app:assembleRelease
```

تأتي معاملات البناء من `version.properties`. الحد الأدنى الحالي لإصدار SDK هو 24 وإصدار SDK المستهدف هو 36.

******

### تخطيط الموارد

******

```text
.readme/lang_*.json
.changelog/lang_*.json
.python/generate_markdown.py
app/src/main/assets/doc/CHANGELOG-*.md
app/src/main/res/values-*/strings.xml
app/src/main/res/raw-*/plugin_instruction.md
```

يوفر `strings.xml` ترجمة بيانات الملحق ونصوص الواجهة. يوفر `plugin_instruction.md` تعليمات الاستخدام والأمان. ينشئ `.python/generate_markdown.py` ملفات README وسجل التغييرات المترجمة من مصادر JSON.

******

### الروابط

******

- وثائق AutoJs6: https://docs.autojs6.com
- مشاركة الملفات الآمنة في Android: https://developer.android.com/training/secure-file-sharing
- AndroidX Media3 ExoPlayer: https://developer.android.com/media/media3/exoplayer
