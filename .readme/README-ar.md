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

- يسجل إجراء Explorer رئيسيا للقراءة فقط لملف واحد عبر الإصدار 2 من بروتوكول `org.autojs.plugin.EXPLORER_ACTION` المشترك.
- يستخدم AndroidX Media3 ExoPlayer وPlayerView للتشغيل التلقائي وعناصر التحكم القياسية وتركيز الصوت ومعالجة تحول خرج الصوت إلى حالة noisy والتكامل مع برامج ترميز الجهاز.
- يستعيد موضع التشغيل وحالة التشغيل أو الإيقاف المؤقت بعد إعادة الإنشاء ويبقي الشاشة قيد التشغيل فقط أثناء تشغيل الفيديو فعليا.
- يوفر مدخلا مستقلا ومصدرا للإجراء `android.intent.action.VIEW` من أجل content URI للقراءة فقط بنوع MIME يطابق `video/*`.
- يوفر إجراء فتح باستخدام تطبيق آخر بشكل آمن بعد فشل التشغيل عبر إعادة بناء Intent عرض للقراءة فقط واستبعاد هذا الملحق من قائمة التطبيقات المرشحة.

******

### التكامل مع المضيف

******

يستخدم المضيف هذا الملحق لمسار فتح الفيديو الرئيسي وإجراء التشغيل.

بعد تثبيت الملحق وتمكينه وتوثيقه والتأكد من توافقه يؤدي فتح فيديو مطابق إلى تشغيل الإجراء `play-video` بوصفه عارض Explorer الرئيسي.

إذا كان الملحق مفقودا أو معطلا أو غير متاح أو غير متوافق أو تعذر تشغيله يعود المضيف إلى مسار النظام `android.intent.action.VIEW` حتى يتمكن تطبيق فيديو آخر مثبت من معالجة الملف.

يطابق هذا الملحق ملفات الفيديو فقط. يظل تشغيل الصوت وعرض الصور قدرات مستقلة لملحقات أخرى ولا يتم تضمينها في ملف APK هذا.

******

### التنسيقات المدعومة

******

يستخدم إجراء Explorer الرئيسي مطابق MIME فارغا ولا يطابق بدقة سوى امتدادات الفيديو الـ 23 التالية للمضيف:

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
Explorer MIME types: empty
external view action: android.intent.action.VIEW
external MIME type: video/*
required host build: 5269
```

يوفر الإصدار 1 إجراء Explorer رئيسيا للقراءة فقط لامتدادات الفيديو المدرجة. يقبل المدخل الخارجي المستقل content URI للقراءة فقط مع أي نوع MIME فرعي صالح من `video/*`. يعتمد فك الترميز الفعلي على مستخرجات Media3 وبرامج الترميز المتاحة على الجهاز.

يتطلب الإصدار 5269 أو أحدث من المضيف.

******

### الأمان

******

لا يطلب الملحق إذن التخزين أو INTERNET. تتحقق حدود Explorer المحمية بالتوقيع من الإصدار 2 للبروتوكول وسطح المصدر وبناء المضيف وcontent URI للهدف والأصل وترتيب ClipData الدقيق واسم العرض والحجم المعلن ونوع MIME والامتداد وإشارات منح القراءة فقط. ثم تنشئ Intent صريحا جديدا للمشغل غير المصدر لا يحتوي إلا على URI الهدف ونوع MIME واسم عرض آمن وعنصر ClipData واحد للهدف ومنح القراءة. تقبل حدود ACTION_VIEW العامة بشكل مستقل content URI ونوع MIME للفيديو ومنح القراءة الدقيق فقط وتتجاهل جميع extras وClipData الواردة وتعيد بناء الطلب الداخلي المصغر نفسه.

******

### حدود الأمان

******

- content URI هدف واحد لكل طلب تشغيل.
- يتطلب تنفيذ Explorer إذن المستوى signature المسمى `org.autojs.permission.PLUGIN`.
- يتم دائما رفض منح الكتابة والمنح الدائمة. لا تتم إعادة توجيه وصول prefix المستخدم للتحقق من URI الأصل إلى المشغل.
- ترفض حدود ACTION_VIEW العامة منح الكتابة والمنح الدائمة ومنح prefix.
- يتم حل التطبيقات المرشحة الخارجية أولا وتصفيتها للاحتفاظ بالحزم الأخرى وتشغيلها باستخدام Intent جديد للقراءة فقط.
- لا يضمن الامتداد المدرج دعم فك الترميز على كل جهاز. يحدد Media3 وبرامج ترميز النظام المثبتة دعم التشغيل الفعلي.

******

### سجل الإصدارات

******

# v1.0.1

###### 2026/08/08

* `إصلاح` إرجاع ارتباط صالح بخدمة Explorer Action عند التمكين من مركز المكونات الإضافية
* `تحسين` اختصار اسم المكون الإضافي ووصفه وصياغة وثائق المستخدم بلغة أكثر طبيعية

# v1.0.0

###### 2026/08/02

* `ميزة` ملحق Video Player بمعرف الملحق `video-player` ومعرف الإجراء `play-video` والمحرك `explorer-action` والمتغير `default`
* `ميزة` إجراء Explorer رئيسي للقراءة فقط عبر الإصدار 2 من البروتوكول مع مطابق MIME فارغ, لا يطابق بدقة سوى امتدادات الفيديو الـ 23 الحالية للمضيف ويشترط بناء المضيف رقم 5269
* `ميزة` مدخل Explorer محمي بالتوقيع مع تحقق صارم من content URI للهدف والأصل وClipData والمصدر واسم العرض والحجم ونوع MIME والامتداد والمنح ثم إعادة توجيه مصغرة إلى مشغل غير مصدر
* `ميزة` مدخل ACTION_VIEW مستقل ومصدر من أجل content URI للفيديو للقراءة فقط مع تجاهل extras وClipData غير الموثوقة ورفض المنح المحظورة والحماية من الحلقة الذاتية
* `ميزة` تشغيل Media3 ExoPlayer وPlayerView مع التشغيل التلقائي وعناصر التحكم القياسية وتركيز الصوت ومعالجة تحول خرج الصوت إلى حالة noisy وحفظ الموضع وحالة التشغيل وإبقاء الشاشة قيد التشغيل أثناء التشغيل الفعلي فقط
* `ميزة` استعادة آمنة باستخدام الفتح بتطبيق آخر بعد فشل التشغيل عبر Intent جديد للقراءة فقط يستبعد هذا الملحق صراحة
* `ميزة` بيانات الملحق ونصوص الواجهة وتعليمات الاستخدام وملفات README وسجلات التغييرات المترجمة إلى الإسبانية والفرنسية والروسية والعربية واليابانية والكورية والإنجليزية والصينية المبسطة والصينية التقليدية لهونغ كونغ والصينية التقليدية لتايوان
* `تبعية` إضافة AndroidX Media3 ExoPlayer وUI الإصدار 1.10.1
* `تبعية` إضافة بيئة تشغيل Kotlin Parcelize الإصدار 2.2.21

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
