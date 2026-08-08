package com.example.appdemo

// These are storage keys, not display text: they are written into each record's JSON and into
// WebDAV backups. Translating them would orphan every existing record, so they stay fixed and
// MilkTeaStrings.sugar / .ice / .cupSize map a key to the label for the current language.
internal val sugarOptions = listOf("无糖", "三分糖", "五分糖", "七分糖", "全糖")
internal val iceOptions = listOf("热", "常温", "去冰", "少冰", "正常冰")
internal val cupSizeOptions = listOf("中杯", "大杯", "超大杯")
