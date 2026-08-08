package com.example.appdemo

import android.content.Context
import androidx.compose.runtime.staticCompositionLocalOf
import java.util.Locale

/**
 * In-app language for the MilkTea flavor. [storageKey] is persisted, so the values must stay
 * stable even if the display names change.
 */
enum class AppLanguage(val storageKey: String) {
    ZhHans("zh-Hans"),
    ZhHant("zh-Hant"),
    English("en"),
    ;

    companion object {
        fun fromStorageKey(key: String?): AppLanguage =
            entries.firstOrNull { it.storageKey == key } ?: ZhHans
    }
}

/**
 * Every user-facing string in the MilkTea app. Anything with a runtime value is a lambda rather
 * than a template, because word order differs between languages.
 *
 * Strings that are *stored* (the sugar/ice/cup-size options, the WebDAV record type) are NOT in
 * here: those stay as fixed Simplified Chinese keys so records and cloud backups written by one
 * language still load in another. Only their display labels are translated.
 */
data class MilkTeaStrings(
    val languageName: String,
    val locale: Locale,

    // Date/time patterns
    val monthPattern: String,
    val yearPattern: String,
    val datePattern: String,
    val timePattern: String,
    val monthDayPattern: String,

    // Common
    val confirm: String,
    val cancel: String,
    val save: String,
    val close: String,
    val edit: String,
    val delete: String,
    val restore: String,
    val back: String,
    val none: String,
    val notFilled: String,
    val empty: String,
    val noRecords: String,

    // Bottom navigation
    val navCalendar: String,
    val navRecords: String,
    val navStats: String,
    val navSettings: String,

    // Calendar
    val today: String,
    val prevMonth: String,
    val nextMonth: String,
    val goRecord: String,
    val noDrinkThisDay: String,
    val dayRecordsTitle: (String) -> String,
    val weekLabels: List<String>,
    val weekFullLabels: List<String>,

    // Record detail
    val detailTitle: String,
    val detailTime: (String) -> String,
    val detailBrand: (String) -> String,
    val detailProduct: (String) -> String,
    val detailCupSize: (String) -> String,
    val detailSugar: (String) -> String,
    val detailIce: (String) -> String,
    val detailAmount: (String) -> String,
    val detailNote: (String) -> String,
    val noteLine: (String) -> String,

    // Record form
    val tabNewRecord: String,
    val tabAllRecords: String,
    val brandLabel: String,
    val brandPlaceholder: String,
    val productLabel: String,
    val productPlaceholder: String,
    val amountLabel: String,
    val amountPlaceholder: String,
    val drinkTime: (String) -> String,
    val pickDate: String,
    val nowButton: String,
    val sugarTitle: String,
    val iceTitle: String,
    val cupSizeTitle: String,
    val noteLabel: String,
    val notePlaceholder: String,
    val submitRecord: String,
    val searchLabel: String,
    val searchPlaceholder: String,
    val emptyNoRecords: String,
    val emptyNoMatch: String,
    val editRecordTitle: String,

    // Stored option labels (keys stay Simplified Chinese, see class doc)
    val sugar: (String) -> String,
    val ice: (String) -> String,
    val cupSize: (String) -> String,
    val dayPeriod: (String) -> String,

    // Stats
    val tabWeekly: String,
    val tabMonthly: String,
    val tabYearly: String,
    val prevPeriod: (StatsMode) -> String,
    val nextPeriod: (StatsMode) -> String,
    // Takes the count so English can pick the singular; the Chinese tables ignore it.
    val cupsOfMilkTea: (Int) -> String,
    val spendingLabel: String,
    val favoriteLabel: String,
    val favoriteValue: (String, Int) -> String,
    val spendTrendTitle: (StatsMode) -> String,
    val cupTrendTitle: (StatsMode) -> String,
    val avgPriceLabel: String,
    val mostExpensiveLabel: String,
    val brandRankingTitle: String,
    val chipSpending: String,
    val chipCups: String,
    val brandSubSpending: (Int, String) -> String,
    val brandSubCups: (String) -> String,
    val brandValueSpending: (String) -> String,
    val brandValueCups: (Int) -> String,
    val currentStreakLabel: String,
    val longestStreakLabel: String,
    val daysValue: (Int) -> String,
    val compareTitle: (StatsMode) -> String,
    val comparePrevEmpty: (StatsMode) -> String,
    val comparePrevValue: (StatsMode, String) -> String,
    val cupUnit: String,
    val peakLabel: (String) -> String,
    val monthShort: (Int) -> String,

    // Settings
    val webdavRowTitle: String,
    val webdavRowSubtitle: String,
    val trashRowTitle: String,
    val trashEmptySubtitle: String,
    val trashRowSubtitle: (Int) -> String,
    val trashEmptyBody: String,
    val trashRemaining: (Int) -> String,
    val deleteForeverTitle: String,
    val deleteForeverBody: String,
    val deleteForeverAction: String,
    val languageRowTitle: String,

    // WebDAV backup
    val webdavTitle: String,
    val webdavRecordTypeLabel: String,
    val webdavIntro: (String) -> String,
    val webdavUrlLabel: String,
    val webdavUsernameLabel: String,
    val webdavPasswordLabel: String,
    val webdavBackupPasswordLabel: String,
    val webdavBackupPasswordHint: String,
    val webdavTestButton: String,
    val webdavSaveButton: String,
    val webdavUploadButton: String,
    val webdavDownloadButton: String,
    val webdavErrUrl: String,
    val webdavErrHttps: String,
    val webdavErrUsername: String,
    val webdavErrPassword: String,
    val webdavErrBackupPassword: String,
    val webdavProcessing: String,
    val webdavGenericError: String,
    val webdavTestOk: String,
    val webdavConfigSaved: String,
    val webdavBackupOk: (Int) -> String,
    val webdavRestoreOk: (Int) -> String,
    val webdavLastBackup: (String) -> String,
    val webdavRestoreConfirmTitle: String,
    val webdavRestoreConfirmBody: (String) -> String,
    val webdavRestoreConfirmAction: String,
    val webdavActionWriteTest: String,
    val webdavActionCleanup: String,
    val webdavActionUpload: String,
    val webdavActionDownload: String,
    val webdavErrNoBackup: String,
    val webdavErrVersion: String,
    val webdavErrTypeMismatch: String,
    val webdavErrBadPassword: String,
    val webdavErrBadFormat: String,
    val webdavErrUnauthorized: String,
    val webdavErrForbiddenInfini: String,
    val webdavErrForbidden: String,
    val webdavErrNotFound: String,
    val webdavErrHttp: (String, Int) -> String,
    val webdavErrWithDetail: (String, String) -> String,
)

private fun statsUnitZhHans(mode: StatsMode) = when (mode) {
    StatsMode.Week -> "周"
    StatsMode.Month -> "月"
    StatsMode.Year -> "年"
}

private fun statsUnitZhHant(mode: StatsMode) = when (mode) {
    StatsMode.Week -> "週"
    StatsMode.Month -> "月"
    StatsMode.Year -> "年"
}

private fun statsUnitEn(mode: StatsMode) = when (mode) {
    StatsMode.Week -> "week"
    StatsMode.Month -> "month"
    StatsMode.Year -> "year"
}

private val enMonthsShort =
    listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")

private fun enPlural(count: Int, singular: String, plural: String = singular + "s") =
    if (count == 1) singular else plural

internal val MilkTeaStringsZhHans = MilkTeaStrings(
    languageName = "简体中文",
    locale = Locale.SIMPLIFIED_CHINESE,
    monthPattern = "yyyy年MM月",
    yearPattern = "yyyy年",
    datePattern = "yyyy-MM-dd",
    timePattern = "yyyy-MM-dd HH:mm",
    monthDayPattern = "MM-dd",
    confirm = "确定",
    cancel = "取消",
    save = "保存",
    close = "关闭",
    edit = "编辑",
    delete = "删除",
    restore = "恢复",
    back = "‹ 返回",
    none = "无",
    notFilled = "未填写",
    empty = "暂无",
    noRecords = "暂无记录",
    navCalendar = "日历",
    navRecords = "记录",
    navStats = "统计",
    navSettings = "设置",
    today = "今天",
    prevMonth = "‹ 上月",
    nextMonth = "下月 ›",
    goRecord = "去记录",
    noDrinkThisDay = "这天还没有喝奶茶。",
    dayRecordsTitle = { date -> "$date 记录" },
    weekLabels = listOf("日", "一", "二", "三", "四", "五", "六"),
    weekFullLabels = listOf("周日", "周一", "周二", "周三", "周四", "周五", "周六"),
    detailTitle = "详情",
    detailTime = { "时间：$it" },
    detailBrand = { "品牌：$it" },
    detailProduct = { "品名：$it" },
    detailCupSize = { "杯型：$it" },
    detailSugar = { "糖度：$it" },
    detailIce = { "温度/冰度：$it" },
    detailAmount = { "金额：$it 元" },
    detailNote = { "备注：$it" },
    noteLine = { "备注：$it" },
    tabNewRecord = "新增记录",
    tabAllRecords = "全部记录",
    brandLabel = "品牌/店名",
    brandPlaceholder = "例如：喜茶、奈雪、霸王茶姬",
    productLabel = "品名",
    productPlaceholder = "例如：多肉葡萄、伯牙绝弦",
    amountLabel = "金额（元）",
    amountPlaceholder = "例如：18.5",
    drinkTime = { "喝奶茶时间：$it" },
    pickDate = "选日期",
    nowButton = "现在",
    sugarTitle = "糖度",
    iceTitle = "温度/冰度",
    cupSizeTitle = "杯型",
    noteLabel = "备注",
    notePlaceholder = "例如：加珍珠、少奶、排队很久",
    submitRecord = "记录一杯",
    searchLabel = "搜索品牌、品名或备注",
    searchPlaceholder = "例如：苦瓜、喜茶",
    emptyNoRecords = "还没有记录，先打第一杯吧。",
    emptyNoMatch = "没有匹配的记录。",
    editRecordTitle = "编辑记录",
    sugar = { it },
    ice = { it },
    cupSize = { it },
    dayPeriod = { it },
    tabWeekly = "周报",
    tabMonthly = "月报",
    tabYearly = "年报",
    prevPeriod = { "‹ 上一${statsUnitZhHans(it)}" },
    nextPeriod = { "下一${statsUnitZhHans(it)} ›" },
    cupsOfMilkTea = { "杯奶茶" },
    spendingLabel = "花费",
    favoriteLabel = "最常喝",
    favoriteValue = { brand, count -> "$brand·${count}杯" },
    spendTrendTitle = { "${statsUnitZhHans(it)}花费趋势" },
    cupTrendTitle = { "${statsUnitZhHans(it)}杯数趋势" },
    avgPriceLabel = "平均单价",
    mostExpensiveLabel = "最贵一杯",
    brandRankingTitle = "品牌排行",
    chipSpending = "花费",
    chipCups = "杯数",
    brandSubSpending = { cups, avg -> "${cups}杯 · 均价￥$avg" },
    brandSubCups = { total -> "共￥$total" },
    brandValueSpending = { "￥$it" },
    brandValueCups = { "${it}杯" },
    currentStreakLabel = "当前连续",
    longestStreakLabel = "历史最长",
    daysValue = { "$it 天" },
    compareTitle = { "与上${statsUnitZhHans(it)}对比" },
    comparePrevEmpty = { "上${statsUnitZhHans(it)}暂无数据" },
    comparePrevValue = { mode, amount -> "上${statsUnitZhHans(mode)}：￥$amount" },
    cupUnit = "杯",
    peakLabel = { "峰值：$it" },
    monthShort = { "${it}月" },
    webdavRowTitle = "WebDAV 云备份",
    webdavRowSubtitle = "配置云端备份与恢复",
    trashRowTitle = "回收站",
    trashEmptySubtitle = "暂无待恢复的记录",
    trashRowSubtitle = { "$it 条待恢复，30 天后自动清空" },
    trashEmptyBody = "回收站是空的。",
    trashRemaining = { "剩余 $it 天后自动清空" },
    deleteForeverTitle = "彻底删除？",
    deleteForeverBody = "这条记录将被永久删除，无法恢复。",
    deleteForeverAction = "彻底删除",
    languageRowTitle = "语言",
    webdavTitle = "WebDAV 云备份",
    webdavRecordTypeLabel = "奶茶记录",
    webdavIntro = { "当前备份：$it。记录会先在手机端加密，再上传到 WebDAV。" },
    webdavUrlLabel = "WebDAV 目录地址",
    webdavUsernameLabel = "用户名",
    webdavPasswordLabel = "WebDAV 密码",
    webdavBackupPasswordLabel = "备份加密密码",
    webdavBackupPasswordHint = "换手机恢复时需要，请务必记住（至少 8 位）",
    webdavTestButton = "测试连接",
    webdavSaveButton = "保存配置",
    webdavUploadButton = "备份到云端",
    webdavDownloadButton = "从云端恢复",
    webdavErrUrl = "请填写 WebDAV 目录地址",
    webdavErrHttps = "为保护记录，只允许使用 HTTPS 地址",
    webdavErrUsername = "请填写用户名",
    webdavErrPassword = "请填写 WebDAV 密码",
    webdavErrBackupPassword = "备份密码至少需要 8 位",
    webdavProcessing = "正在处理…",
    webdavGenericError = "操作失败",
    webdavTestOk = "连接成功",
    webdavConfigSaved = "配置已保存",
    webdavBackupOk = { "备份成功，共 $it 条记录" },
    webdavRestoreOk = { "恢复成功，共 $it 条记录" },
    webdavLastBackup = { "最近备份：$it" },
    webdavRestoreConfirmTitle = "恢复云端备份？",
    webdavRestoreConfirmBody = { "恢复会用云端备份覆盖当前设备上的全部$it。此操作无法撤销。" },
    webdavRestoreConfirmAction = "确认恢复",
    webdavActionWriteTest = "写入测试",
    webdavActionCleanup = "清理测试文件",
    webdavActionUpload = "上传",
    webdavActionDownload = "下载",
    webdavErrNoBackup = "云端还没有备份文件",
    webdavErrVersion = "不支持的备份版本",
    webdavErrTypeMismatch = "云端备份类型不匹配",
    webdavErrBadPassword = "备份密码错误或备份文件已损坏",
    webdavErrBadFormat = "备份文件格式无效",
    webdavErrUnauthorized = "认证失败，请检查用户名和 WebDAV 专用密码",
    webdavErrForbiddenInfini = "服务器拒绝写入。InfiniCLOUD 请使用 WebDAV 连接 ID 和专用密码，并确认目录已存在且可写",
    webdavErrForbidden = "服务器拒绝写入，请检查账号写入权限和目录地址",
    webdavErrNotFound = "目录不存在，请先在 WebDAV 中创建该目录",
    webdavErrHttp = { action, code -> "$action 失败，服务器返回 HTTP $code" },
    webdavErrWithDetail = { message, detail -> "$message（$detail）" },
)

internal val MilkTeaStringsZhHant = MilkTeaStrings(
    languageName = "繁體中文",
    locale = Locale.TRADITIONAL_CHINESE,
    monthPattern = "yyyy年MM月",
    yearPattern = "yyyy年",
    datePattern = "yyyy-MM-dd",
    timePattern = "yyyy-MM-dd HH:mm",
    monthDayPattern = "MM-dd",
    confirm = "確定",
    cancel = "取消",
    save = "儲存",
    close = "關閉",
    edit = "編輯",
    delete = "刪除",
    restore = "還原",
    back = "‹ 返回",
    none = "無",
    notFilled = "未填寫",
    empty = "暫無",
    noRecords = "暫無紀錄",
    navCalendar = "日曆",
    navRecords = "紀錄",
    navStats = "統計",
    navSettings = "設定",
    today = "今天",
    prevMonth = "‹ 上月",
    nextMonth = "下月 ›",
    goRecord = "去記錄",
    noDrinkThisDay = "這天還沒有喝奶茶。",
    dayRecordsTitle = { date -> "$date 紀錄" },
    weekLabels = listOf("日", "一", "二", "三", "四", "五", "六"),
    weekFullLabels = listOf("週日", "週一", "週二", "週三", "週四", "週五", "週六"),
    detailTitle = "詳情",
    detailTime = { "時間：$it" },
    detailBrand = { "品牌：$it" },
    detailProduct = { "品名：$it" },
    detailCupSize = { "杯型：$it" },
    detailSugar = { "糖度：$it" },
    detailIce = { "溫度/冰塊：$it" },
    detailAmount = { "金額：$it 元" },
    detailNote = { "備註：$it" },
    noteLine = { "備註：$it" },
    tabNewRecord = "新增紀錄",
    tabAllRecords = "全部紀錄",
    brandLabel = "品牌/店名",
    brandPlaceholder = "例如：喜茶、奈雪、霸王茶姬",
    productLabel = "品名",
    productPlaceholder = "例如：多肉葡萄、伯牙絕弦",
    amountLabel = "金額（元）",
    amountPlaceholder = "例如：18.5",
    drinkTime = { "喝奶茶時間：$it" },
    pickDate = "選日期",
    nowButton = "現在",
    sugarTitle = "糖度",
    iceTitle = "溫度/冰塊",
    cupSizeTitle = "杯型",
    noteLabel = "備註",
    notePlaceholder = "例如：加珍珠、少奶、排隊很久",
    submitRecord = "記錄一杯",
    searchLabel = "搜尋品牌、品名或備註",
    searchPlaceholder = "例如：苦瓜、喜茶",
    emptyNoRecords = "還沒有紀錄，先喝第一杯吧。",
    emptyNoMatch = "沒有符合的紀錄。",
    editRecordTitle = "編輯紀錄",
    sugar = { key ->
        when (key) {
            "无糖" -> "無糖"
            "三分糖" -> "三分糖"
            "五分糖" -> "五分糖"
            "七分糖" -> "七分糖"
            "全糖" -> "全糖"
            else -> key
        }
    },
    ice = { key ->
        when (key) {
            "热" -> "熱"
            "常温" -> "常溫"
            "去冰" -> "去冰"
            "少冰" -> "少冰"
            "正常冰" -> "正常冰"
            else -> key
        }
    },
    cupSize = { key ->
        when (key) {
            "中杯" -> "中杯"
            "大杯" -> "大杯"
            "超大杯" -> "超大杯"
            else -> key
        }
    },
    dayPeriod = { key ->
        when (key) {
            "早上" -> "早上"
            "下午" -> "下午"
            "晚上" -> "晚上"
            else -> key
        }
    },
    tabWeekly = "週報",
    tabMonthly = "月報",
    tabYearly = "年報",
    prevPeriod = { "‹ 上一${statsUnitZhHant(it)}" },
    nextPeriod = { "下一${statsUnitZhHant(it)} ›" },
    cupsOfMilkTea = { "杯奶茶" },
    spendingLabel = "花費",
    favoriteLabel = "最常喝",
    favoriteValue = { brand, count -> "$brand·${count}杯" },
    spendTrendTitle = { "${statsUnitZhHant(it)}花費趨勢" },
    cupTrendTitle = { "${statsUnitZhHant(it)}杯數趨勢" },
    avgPriceLabel = "平均單價",
    mostExpensiveLabel = "最貴一杯",
    brandRankingTitle = "品牌排行",
    chipSpending = "花費",
    chipCups = "杯數",
    brandSubSpending = { cups, avg -> "${cups}杯 · 均價￥$avg" },
    brandSubCups = { total -> "共￥$total" },
    brandValueSpending = { "￥$it" },
    brandValueCups = { "${it}杯" },
    currentStreakLabel = "目前連續",
    longestStreakLabel = "歷史最長",
    daysValue = { "$it 天" },
    compareTitle = { "與上${statsUnitZhHant(it)}比較" },
    comparePrevEmpty = { "上${statsUnitZhHant(it)}暫無資料" },
    comparePrevValue = { mode, amount -> "上${statsUnitZhHant(mode)}：￥$amount" },
    cupUnit = "杯",
    peakLabel = { "峰值：$it" },
    monthShort = { "${it}月" },
    webdavRowTitle = "WebDAV 雲端備份",
    webdavRowSubtitle = "設定雲端備份與還原",
    trashRowTitle = "回收筒",
    trashEmptySubtitle = "暫無待還原的紀錄",
    trashRowSubtitle = { "$it 筆待還原，30 天後自動清空" },
    trashEmptyBody = "回收筒是空的。",
    trashRemaining = { "剩餘 $it 天後自動清空" },
    deleteForeverTitle = "徹底刪除？",
    deleteForeverBody = "這筆紀錄將被永久刪除，無法還原。",
    deleteForeverAction = "徹底刪除",
    languageRowTitle = "語言",
    webdavTitle = "WebDAV 雲端備份",
    webdavRecordTypeLabel = "奶茶紀錄",
    webdavIntro = { "目前備份：$it。紀錄會先在手機端加密，再上傳到 WebDAV。" },
    webdavUrlLabel = "WebDAV 目錄位址",
    webdavUsernameLabel = "使用者名稱",
    webdavPasswordLabel = "WebDAV 密碼",
    webdavBackupPasswordLabel = "備份加密密碼",
    webdavBackupPasswordHint = "換手機還原時需要，請務必記住（至少 8 位）",
    webdavTestButton = "測試連線",
    webdavSaveButton = "儲存設定",
    webdavUploadButton = "備份到雲端",
    webdavDownloadButton = "從雲端還原",
    webdavErrUrl = "請填寫 WebDAV 目錄位址",
    webdavErrHttps = "為保護紀錄，只允許使用 HTTPS 位址",
    webdavErrUsername = "請填寫使用者名稱",
    webdavErrPassword = "請填寫 WebDAV 密碼",
    webdavErrBackupPassword = "備份密碼至少需要 8 位",
    webdavProcessing = "處理中…",
    webdavGenericError = "操作失敗",
    webdavTestOk = "連線成功",
    webdavConfigSaved = "設定已儲存",
    webdavBackupOk = { "備份成功，共 $it 筆紀錄" },
    webdavRestoreOk = { "還原成功，共 $it 筆紀錄" },
    webdavLastBackup = { "最近備份：$it" },
    webdavRestoreConfirmTitle = "還原雲端備份？",
    webdavRestoreConfirmBody = { "還原會用雲端備份覆蓋目前裝置上的全部$it。此操作無法復原。" },
    webdavRestoreConfirmAction = "確認還原",
    webdavActionWriteTest = "寫入測試",
    webdavActionCleanup = "清理測試檔案",
    webdavActionUpload = "上傳",
    webdavActionDownload = "下載",
    webdavErrNoBackup = "雲端還沒有備份檔案",
    webdavErrVersion = "不支援的備份版本",
    webdavErrTypeMismatch = "雲端備份類型不符",
    webdavErrBadPassword = "備份密碼錯誤或備份檔案已損毀",
    webdavErrBadFormat = "備份檔案格式無效",
    webdavErrUnauthorized = "驗證失敗，請檢查使用者名稱和 WebDAV 專用密碼",
    webdavErrForbiddenInfini = "伺服器拒絕寫入。InfiniCLOUD 請使用 WebDAV 連線 ID 和專用密碼，並確認目錄已存在且可寫",
    webdavErrForbidden = "伺服器拒絕寫入，請檢查帳號寫入權限和目錄位址",
    webdavErrNotFound = "目錄不存在，請先在 WebDAV 中建立該目錄",
    webdavErrHttp = { action, code -> "$action 失敗，伺服器回傳 HTTP $code" },
    webdavErrWithDetail = { message, detail -> "$message（$detail）" },
)

internal val MilkTeaStringsEn = MilkTeaStrings(
    languageName = "English",
    locale = Locale.ENGLISH,
    monthPattern = "MMMM yyyy",
    yearPattern = "yyyy",
    datePattern = "yyyy-MM-dd",
    timePattern = "yyyy-MM-dd HH:mm",
    monthDayPattern = "MM-dd",
    confirm = "OK",
    cancel = "Cancel",
    save = "Save",
    close = "Close",
    edit = "Edit",
    delete = "Delete",
    restore = "Restore",
    back = "‹ Back",
    none = "None",
    notFilled = "Not set",
    empty = "None yet",
    noRecords = "No records",
    navCalendar = "Calendar",
    navRecords = "Records",
    navStats = "Stats",
    navSettings = "Settings",
    today = "Today",
    prevMonth = "‹ Prev",
    nextMonth = "Next ›",
    goRecord = "Add one",
    noDrinkThisDay = "No milk tea on this day.",
    dayRecordsTitle = { date -> "Records for $date" },
    weekLabels = listOf("S", "M", "T", "W", "T", "F", "S"),
    weekFullLabels = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"),
    detailTitle = "Details",
    detailTime = { "Time: $it" },
    detailBrand = { "Brand: $it" },
    detailProduct = { "Drink: $it" },
    detailCupSize = { "Size: $it" },
    detailSugar = { "Sugar: $it" },
    detailIce = { "Temp/Ice: $it" },
    detailAmount = { "Price: ￥$it" },
    detailNote = { "Note: $it" },
    noteLine = { "Note: $it" },
    tabNewRecord = "Add record",
    tabAllRecords = "All records",
    brandLabel = "Brand / shop",
    brandPlaceholder = "e.g. HEYTEA, Nayuki, Chagee",
    productLabel = "Drink name",
    productPlaceholder = "e.g. Grape Cheezo, Boya Juexian",
    amountLabel = "Price (￥)",
    amountPlaceholder = "e.g. 18.5",
    drinkTime = { "Drink time: $it" },
    pickDate = "Pick date",
    nowButton = "Now",
    sugarTitle = "Sugar",
    iceTitle = "Temperature / ice",
    cupSizeTitle = "Cup size",
    noteLabel = "Note",
    notePlaceholder = "e.g. extra boba, less milk, long queue",
    submitRecord = "Log a cup",
    searchLabel = "Search brand, drink or note",
    searchPlaceholder = "e.g. bitter gourd, HEYTEA",
    emptyNoRecords = "No records yet — log your first cup.",
    emptyNoMatch = "No matching records.",
    editRecordTitle = "Edit record",
    sugar = { key ->
        when (key) {
            "无糖" -> "No sugar"
            "三分糖" -> "30% sugar"
            "五分糖" -> "50% sugar"
            "七分糖" -> "70% sugar"
            "全糖" -> "Full sugar"
            else -> key
        }
    },
    ice = { key ->
        when (key) {
            "热" -> "Hot"
            "常温" -> "Room temp"
            "去冰" -> "No ice"
            "少冰" -> "Less ice"
            "正常冰" -> "Regular ice"
            else -> key
        }
    },
    cupSize = { key ->
        when (key) {
            "中杯" -> "Tall"
            "大杯" -> "Grande"
            "超大杯" -> "Venti"
            else -> key
        }
    },
    dayPeriod = { key ->
        when (key) {
            "早上" -> "Morning"
            "下午" -> "Afternoon"
            "晚上" -> "Evening"
            else -> key
        }
    },
    tabWeekly = "Weekly",
    tabMonthly = "Monthly",
    tabYearly = "Yearly",
    prevPeriod = { "‹ Prev ${statsUnitEn(it)}" },
    nextPeriod = { "Next ${statsUnitEn(it)} ›" },
    cupsOfMilkTea = { "${enPlural(it, "cup")} of milk tea" },
    spendingLabel = "Spent",
    favoriteLabel = "Most ordered",
    favoriteValue = { brand, count -> "$brand · $count" },
    spendTrendTitle = { "Spending by ${statsUnitEn(it)}" },
    cupTrendTitle = { "Cups by ${statsUnitEn(it)}" },
    avgPriceLabel = "Average price",
    mostExpensiveLabel = "Priciest cup",
    brandRankingTitle = "Brand ranking",
    chipSpending = "Spending",
    chipCups = "Cups",
    brandSubSpending = { cups, avg -> "$cups ${enPlural(cups, "cup")} · avg ￥$avg" },
    brandSubCups = { total -> "￥$total total" },
    brandValueSpending = { "￥$it" },
    brandValueCups = { "$it ${enPlural(it, "cup")}" },
    currentStreakLabel = "Current streak",
    longestStreakLabel = "Longest streak",
    daysValue = { "$it ${enPlural(it, "day")}" },
    compareTitle = { "vs. last ${statsUnitEn(it)}" },
    comparePrevEmpty = { "No data last ${statsUnitEn(it)}" },
    comparePrevValue = { mode, amount -> "Last ${statsUnitEn(mode)}: ￥$amount" },
    cupUnit = "cups",
    peakLabel = { "Peak: $it" },
    monthShort = { enMonthsShort[(it - 1).coerceIn(0, 11)] },
    webdavRowTitle = "WebDAV backup",
    webdavRowSubtitle = "Set up cloud backup and restore",
    trashRowTitle = "Trash",
    trashEmptySubtitle = "Nothing to restore",
    trashRowSubtitle = { "$it ${enPlural(it, "record")} to restore, cleared after 30 days" },
    trashEmptyBody = "Trash is empty.",
    trashRemaining = { "Cleared in $it ${enPlural(it, "day")}" },
    deleteForeverTitle = "Delete forever?",
    deleteForeverBody = "This record will be permanently deleted and cannot be recovered.",
    deleteForeverAction = "Delete forever",
    languageRowTitle = "Language",
    webdavTitle = "WebDAV backup",
    webdavRecordTypeLabel = "milk tea records",
    webdavIntro = { "Backing up: $it. Records are encrypted on this phone before they are uploaded to WebDAV." },
    webdavUrlLabel = "WebDAV folder URL",
    webdavUsernameLabel = "Username",
    webdavPasswordLabel = "WebDAV password",
    webdavBackupPasswordLabel = "Backup encryption password",
    webdavBackupPasswordHint = "Needed to restore on a new phone — keep it safe (at least 8 characters)",
    webdavTestButton = "Test connection",
    webdavSaveButton = "Save settings",
    webdavUploadButton = "Back up to cloud",
    webdavDownloadButton = "Restore from cloud",
    webdavErrUrl = "Enter the WebDAV folder URL",
    webdavErrHttps = "Only HTTPS URLs are allowed, to keep records safe",
    webdavErrUsername = "Enter a username",
    webdavErrPassword = "Enter the WebDAV password",
    webdavErrBackupPassword = "The backup password needs at least 8 characters",
    webdavProcessing = "Working…",
    webdavGenericError = "Something went wrong",
    webdavTestOk = "Connected",
    webdavConfigSaved = "Settings saved",
    webdavBackupOk = { "Backed up $it ${enPlural(it, "record")}" },
    webdavRestoreOk = { "Restored $it ${enPlural(it, "record")}" },
    webdavLastBackup = { "Last backup: $it" },
    webdavRestoreConfirmTitle = "Restore cloud backup?",
    webdavRestoreConfirmBody = { "Restoring overwrites all $it on this device. This cannot be undone." },
    webdavRestoreConfirmAction = "Restore",
    webdavActionWriteTest = "Write test",
    webdavActionCleanup = "Test file cleanup",
    webdavActionUpload = "Upload",
    webdavActionDownload = "Download",
    webdavErrNoBackup = "No backup file in the cloud yet",
    webdavErrVersion = "Unsupported backup version",
    webdavErrTypeMismatch = "Cloud backup type does not match",
    webdavErrBadPassword = "Wrong backup password, or the backup file is corrupted",
    webdavErrBadFormat = "Invalid backup file format",
    webdavErrUnauthorized = "Authentication failed — check the username and WebDAV app password",
    webdavErrForbiddenInfini = "Server refused the write. For InfiniCLOUD use the WebDAV connection ID and app password, and make sure the folder exists and is writable",
    webdavErrForbidden = "Server refused the write — check the account's write permission and the folder URL",
    webdavErrNotFound = "Folder not found — create it in WebDAV first",
    webdavErrHttp = { action, code -> "$action failed, server returned HTTP $code" },
    webdavErrWithDetail = { message, detail -> "$message ($detail)" },
)

internal fun stringsFor(language: AppLanguage): MilkTeaStrings = when (language) {
    AppLanguage.ZhHans -> MilkTeaStringsZhHans
    AppLanguage.ZhHant -> MilkTeaStringsZhHant
    AppLanguage.English -> MilkTeaStringsEn
}

/** Provided once at the root of the app; defaults to Simplified Chinese. */
internal val LocalMilkTeaStrings = staticCompositionLocalOf { MilkTeaStringsZhHans }

private const val LANGUAGE_KEY = "app_language"

internal fun loadLanguage(context: Context): AppLanguage {
    val prefs = context.getSharedPreferences(MILK_TEA_PREFS_NAME, Context.MODE_PRIVATE)
    return AppLanguage.fromStorageKey(prefs.getString(LANGUAGE_KEY, null))
}

internal fun saveLanguage(context: Context, language: AppLanguage) {
    context.getSharedPreferences(MILK_TEA_PREFS_NAME, Context.MODE_PRIVATE)
        .edit()
        .putString(LANGUAGE_KEY, language.storageKey)
        .apply()
}
