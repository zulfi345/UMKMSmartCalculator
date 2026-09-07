package com.example.umkmsmart

import android.app.Activity
import android.app.AlertDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.text.InputType
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.View
import android.widget.*
import java.text.NumberFormat
import java.util.Locale
import org.json.JSONArray
import org.json.JSONObject

class MainActivity : Activity() {

    private val green = Color.rgb(7, 117, 70)
    private val darkGreen = Color.rgb(4, 79, 48)
    private val softGreen = Color.rgb(229, 246, 237)
    private val bgLight = Color.rgb(247, 249, 248)
    private val textDark = Color.rgb(28, 47, 42)
    private val muted = Color.rgb(105, 120, 115)

    private var darkMode = false
    private var notificationsEnabled = true
    private var lowStockThreshold = 5
    private var businessName = "Usaha Saya"
    private var businessOwner = ""
    private var businessPhone = ""
    private var businessAddress = ""
    private var backAction: (() -> Unit)? = null
    private var cashierCategory = "Semua"
    private var cashierQuery = ""
    private val prefs by lazy { getSharedPreferences("umkm_smart", MODE_PRIVATE) }

    data class Product(
        var name: String,
        var sellPrice: Int,
        var buyPrice: Int,
        var stock: Int,
        var category: String,
        var description: String = ""
    )

    data class Sale(
        val title: String,
        val amount: Int,
        val type: String
    )

    private val products = mutableListOf(
        Product("Keripik Singkong", 5000, 3000, 20, "Makanan", "Keripik singkong renyah dan gurih"),
        Product("Pisang Crispy", 8000, 4500, 15, "Makanan", "Pisang crispy dengan topping pilihan"),
        Product("Brownies", 12000, 7000, 8, "Makanan", "Brownies cokelat lembut"),
        Product("Es Teh Manis", 3000, 1200, 30, "Minuman", "Teh manis segar"),
        Product("Kopi Susu", 7000, 3500, 5, "Minuman", "Kopi susu creamy")
    )

    private val cart = mutableMapOf<String, Int>()

    private val transactions = mutableListOf(
        Sale("Keripik Singkong • 5 pcs", 25000, "Penjualan"),
        Sale("Kopi Susu • 3 pcs", 21000, "Penjualan"),
        Sale("Pisang Crispy • 2 pcs", 16000, "Penjualan"),
        Sale("Pembelian bahan baku", 50000, "Pengeluaran")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadState()
        showSplash()
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        val action = backAction
        if (action != null) action.invoke() else super.onBackPressed()
    }

    private fun setBack(action: (() -> Unit)?) {
        backAction = action
    }

    private fun showSplash() {
        setBack(null)
        val root = LinearLayout(this)
        root.orientation = LinearLayout.VERTICAL
        root.gravity = Gravity.CENTER
        root.setPadding(dp(32), dp(32), dp(32), dp(32))
        root.background = GradientDrawable(
            GradientDrawable.Orientation.TL_BR,
            intArrayOf(Color.WHITE, softGreen, Color.rgb(205, 239, 220))
        )

        val logo = ImageView(this)
        logo.setImageResource(R.drawable.vecta_umkm)
        logo.scaleType = ImageView.ScaleType.CENTER_INSIDE

        val title = text("VECTA HUB", 30f, textDark, true, Gravity.CENTER)
        val sub = text("UMKM", 24f, textDark, false, Gravity.CENTER)

        val project = text("VECTA PROJECT", 14f, darkGreen, true, Gravity.CENTER)
        project.setPadding(0, dp(30), 0, dp(8))

        val dots = text("•", 32f, green, true, Gravity.CENTER)

        root.addView(logo, lp(dp(190), dp(190)))
        root.addView(title, lp(-1, -2))
        root.addView(sub, lp(-1, -2))
        root.addView(project, lp(-1, -2))
        root.addView(dots, lp(-1, -2))

        setContentView(root)

        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed({ dots.text = "••" }, 400)
        handler.postDelayed({ dots.text = "•••" }, 800)
        handler.postDelayed({ dots.text = "••••" }, 1200)
        handler.postDelayed({ showLogin() }, 1800)
    }

    private fun showLogin() {
        setBack(null)
        val root = LinearLayout(this)
        root.orientation = LinearLayout.VERTICAL
        root.gravity = Gravity.CENTER
        root.setPadding(dp(28), dp(28), dp(28), dp(28))
        root.background = backgroundColor()

        val logo = ImageView(this)
        logo.setImageResource(R.drawable.vecta_umkm)
        logo.scaleType = ImageView.ScaleType.CENTER_INSIDE

        val card = LinearLayout(this)
        card.orientation = LinearLayout.VERTICAL
        card.setPadding(dp(24), dp(24), dp(24), dp(24))
        card.background = rounded(cardColor(), 28)

        val welcome = text("Selamat Datang", 24f, textColor(), true)
        val desc = text("Masuk untuk melanjutkan mengelola usaha", 14f, muted)

        val username = EditText(this)
        username.hint = "Email / Username"
        username.background = rounded(inputColor(), 14)
        username.setPadding(dp(16), 0, dp(16), 0)

        val password = EditText(this)
        password.hint = "Password"
        password.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        password.background = rounded(inputColor(), 14)
        password.setPadding(dp(16), 0, dp(16), 0)

        val login = button("Masuk")
        login.setOnClickListener { showDashboard() }

        val register = text("Belum punya akun? Daftar", 13f, green, false, Gravity.CENTER)

        card.addView(welcome)
        card.addView(desc, margins(-1, -2, 0, 0, 0, dp(18)))
        card.addView(username, margins(-1, dp(60), 0, 0, 0, dp(12)))
        card.addView(password, margins(-1, dp(60), 0, 0, 0, dp(16)))
        card.addView(login, lp(-1, dp(52)))
        card.addView(register, margins(-1, -2, 0, dp(18), 0, 0))

        root.addView(logo, lp(dp(150), dp(150)))
        root.addView(card, margins(-1, -2, 0, dp(16), 0, 0))

        setContentView(root)
    }

    private fun showDashboard() {
        setBack(null)
        val root = LinearLayout(this)
        root.orientation = LinearLayout.VERTICAL
        root.background = backgroundColor()

        val scroll = ScrollView(this)
        val content = LinearLayout(this)
        content.orientation = LinearLayout.VERTICAL
        content.setPadding(dp(16), dp(16), dp(16), dp(16))

        val header = LinearLayout(this)
        header.orientation = LinearLayout.VERTICAL
        header.setPadding(dp(20), dp(18), dp(20), dp(18))
        header.background = rounded(darkGreen, 24)

        val brand = text("VECTA HUB UMKM", 17f, Color.WHITE, true)
        val hello = text("Halo, Selamat Datang! 👋\nKelola usaha lebih mudah", 14f, Color.WHITE)

        header.addView(brand)
        header.addView(hello, margins(-1, -2, 0, dp(6), 0, 0))

        content.addView(header)

        content.addView(
            statCard("Omzet Hari Ini", "Rp 1.250.000", "↑ 12% dari kemarin"),
            margins(-1, -2, 0, dp(14), 0, 0)
        )

        val row = LinearLayout(this)
        row.orientation = LinearLayout.HORIZONTAL

        row.addView(
            smallStat("Keuntungan", "Rp 420.000"),
            weightMargins(1f, 0, 0, dp(7), 0)
        )

        row.addView(
            smallStat("Produk", "${products.size} Produk"),
            weightMargins(1f, dp(7), 0, 0, 0)
        )

        content.addView(row)

        content.addView(
            sectionTitle("Menu Utama"),
            margins(-1, -2, 0, dp(20), 0, dp(10))
        )

        addMenuGrid(
            content,
            "🛒", "Kasir", { showCashier() },
            "🧮", "Kalkulator", { showCalculator() }
        )

        addMenuGrid(
            content,
            "📦", "Manajemen Stok", { showProducts() },
            "💸", "Catat Pengeluaran", { showExpense() }
        )

        addMenuGrid(
            content,
            "🧾", "Catat Penjualan", { showSaleForm() },
            "📊", "Laporan Usaha", { showReport() }
        )

        addMenuGrid(
            content,
            "🧾", "Transaksi", { showTransactions() },
            "⚙", "Pengaturan", { showSettings() }
        )

        content.addView(
            sectionTitle("Transaksi Terbaru"),
            margins(-1, -2, 0, dp(20), 0, dp(8))
        )

        transactions.take(4).forEach {
            content.addView(
                transactionCard(it),
                margins(-1, -2, 0, 0, 0, dp(8))
            )
        }

        scroll.addView(content)
        root.addView(scroll, LinearLayout.LayoutParams(-1, 0, 1f))
        root.addView(bottomNav("Beranda"))

        setContentView(root)
    }

    private fun showCashier() {
        setBack { showDashboard() }
        val root = LinearLayout(this)
        root.orientation = LinearLayout.VERTICAL
        root.background = backgroundColor()

        root.addView(topBar("Kasir") { showDashboard() })

        val search = EditText(this)
        search.hint = "Cari produk..."
        search.setText(cashierQuery)
        search.textSize = 17f
        search.background = rounded(inputColor(), 14)
        search.setPadding(dp(14), 0, dp(14), 0)
        search.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(e: Editable?) {
                cashierQuery = e?.toString().orEmpty()
            }
        })
        root.addView(search, margins(-1, dp(60), dp(16), dp(12), dp(16), dp(8)))

        val category = LinearLayout(this)
        category.orientation = LinearLayout.HORIZONTAL
        category.setPadding(dp(16), 0, dp(16), dp(8))
        listOf("Semua", "Makanan", "Minuman").forEach { name ->
            val active = cashierCategory == name
            val b = Button(this)
            b.text = name
            b.textSize = 12f
            b.setTextColor(if (active) Color.WHITE else textColor())
            b.background = rounded(if (active) green else inputColor(), 14)
            b.setOnClickListener { cashierCategory = name; showCashier() }
            category.addView(b, weightMargins(1f, if (name == "Semua") 0 else dp(4), 0, 0, 0))
        }
        root.addView(category)

        val scroll = ScrollView(this)
        val list = LinearLayout(this)
        list.orientation = LinearLayout.VERTICAL
        list.setPadding(dp(16), dp(4), dp(16), dp(16))

        products.filter { product ->
            (cashierCategory == "Semua" || product.category.equals(cashierCategory, true)) &&
            (cashierQuery.isBlank() || product.name.contains(cashierQuery, true))
        }.forEach { product ->
            list.addView(cashierProduct(product), margins(-1, -2, 0, 0, 0, dp(8)))
        }

        scroll.addView(list)
        root.addView(scroll, LinearLayout.LayoutParams(-1, 0, 1f))

        var itemCount = 0
        var total = 0

        cart.forEach { (name, qty) ->
            val p = products.find { it.name == name }
            if (p != null) {
                itemCount += qty
                total += p.sellPrice * qty
            }
        }

        val bottom = LinearLayout(this)
        bottom.orientation = LinearLayout.HORIZONTAL
        bottom.gravity = Gravity.CENTER_VERTICAL
        bottom.setPadding(dp(16), dp(10), dp(16), dp(12))
        bottom.background = cardColor()

        val totalText = text("$itemCount Item | ${rupiah(total)}", 15f, textColor(), true)

        val pay = button("Bayar")
        pay.setOnClickListener { payCart() }

        bottom.addView(totalText, LinearLayout.LayoutParams(0, dp(52), 1f))
        bottom.addView(pay, lp(dp(110), dp(52)))

        root.addView(bottom)
        root.addView(bottomNav("Kasir"))

        setContentView(root)
    }

    private fun cashierProduct(product: Product): View {
        val row = LinearLayout(this)
        row.gravity = Gravity.CENTER_VERTICAL
        row.setPadding(dp(14), dp(12), dp(14), dp(12))
        row.background = rounded(cardColor(), 18)

        val icon = text(if (product.category == "Minuman") "🥤" else "🍟", 26f, textColor(), false, Gravity.CENTER)
        val info = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        info.addView(text(product.name, 16f, textColor(), true))
        info.addView(text("${rupiah(product.sellPrice)}   •   Stok: ${product.stock}", 12f, muted))

        val controls = LinearLayout(this).apply { gravity = Gravity.CENTER_VERTICAL }
        val minus = Button(this).apply {
            text = "−"; textSize = 20f; setTextColor(textColor()); background = rounded(inputColor(), 50)
            setOnClickListener {
                val qty = cart[product.name] ?: 0
                if (qty > 1) cart[product.name] = qty - 1 else cart.remove(product.name)
                showCashier()
            }
        }
        val qtyText = text("${cart[product.name] ?: 0}", 15f, textColor(), true, Gravity.CENTER)
        val add = Button(this).apply {
            text = "+"; textSize = 20f; setTextColor(Color.WHITE); background = rounded(green, 50)
            setOnClickListener {
                val inCart = cart[product.name] ?: 0
                if (product.stock - inCart <= 0) toast("Jumlah di keranjang sudah mencapai stok")
                else { cart[product.name] = inCart + 1; showCashier() }
            }
        }
        controls.addView(minus, lp(dp(46), dp(46)))
        controls.addView(qtyText, lp(dp(34), dp(46)))
        controls.addView(add, lp(dp(46), dp(46)))

        row.addView(icon, lp(dp(48), dp(48)))
        row.addView(info, LinearLayout.LayoutParams(0, -2, 1f))
        row.addView(controls)
        return row
    }

    private fun payCart() {
        if (cart.isEmpty()) {
            toast("Keranjang masih kosong")
            return
        }

        var total = 0
        cart.forEach { (name, qty) ->
            products.find { it.name == name }?.let {
                total += it.sellPrice * qty
            }
        }

        AlertDialog.Builder(this)
            .setTitle("Pembayaran")
            .setMessage("Total pembayaran\n\n${rupiah(total)}\n\nPilih metode pembayaran")
            .setPositiveButton("Tunai") { _, _ -> completePayment(total) }
            .setNeutralButton("QRIS") { _, _ -> completePayment(total) }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun completePayment(total: Int) {
        cart.forEach { (name, qty) ->
            products.find { it.name == name }?.let {
                it.stock = maxOf(0, it.stock - qty)
            }
        }

        transactions.add(0, Sale("Penjualan Kasir", total, "Penjualan"))
        cart.clear()
        saveState()
        toast("Pembayaran berhasil disimpan")
        showDashboard()
    }

    private fun showProducts() {
        setBack { showDashboard() }
        val root = LinearLayout(this)
        root.orientation = LinearLayout.VERTICAL
        root.background = backgroundColor()

        root.addView(topBar("Manajemen Stok") { showDashboard() })

        val add = button("+ Tambah Produk")
        add.setOnClickListener { showAddProduct() }
        root.addView(add, margins(-1, dp(52), dp(16), dp(12), dp(16), dp(8)))

        val scroll = ScrollView(this)
        val content = LinearLayout(this)
        content.orientation = LinearLayout.VERTICAL
        content.setPadding(dp(16), 0, dp(16), dp(16))

        products.forEach { product ->
            val card = LinearLayout(this)
            card.orientation = LinearLayout.VERTICAL
            card.setPadding(dp(16), dp(14), dp(16), dp(14))
            card.background = rounded(cardColor(), 18)

            card.addView(text(product.name, 17f, textColor(), true))
            card.addView(text("Kategori: ${product.category}", 12f, muted))
            card.addView(text("Harga Jual: ${rupiah(product.sellPrice)}", 13f, green, true))
            card.addView(text("Harga Beli: ${rupiah(product.buyPrice)}", 13f, muted))
            card.addView(text("Stok: ${product.stock} pcs", 13f,
                if (product.stock <= 5) Color.rgb(210, 70, 60) else green
            ))

            val detail = button("Detail Produk")
            detail.setOnClickListener { showProductDetail(product) }
            card.addView(detail, margins(-1, dp(46), 0, dp(12), 0, 0))

            content.addView(card, margins(-1, -2, 0, 0, 0, dp(10)))
        }

        scroll.addView(content)
        root.addView(scroll, LinearLayout.LayoutParams(-1, 0, 1f))
        root.addView(bottomNav("Produk"))

        setContentView(root)
    }

    private fun showProductDetail(product: Product) {
        setBack { showProducts() }
        val root = LinearLayout(this)
        root.orientation = LinearLayout.VERTICAL
        root.background = backgroundColor()

        root.addView(topBar("Detail Produk") { showProducts() })

        val content = LinearLayout(this)
        content.orientation = LinearLayout.VERTICAL
        content.setPadding(dp(20), dp(20), dp(20), dp(20))

        content.addView(text(product.name, 25f, textColor(), true))
        content.addView(text(rupiah(product.sellPrice), 19f, green, true))
        content.addView(text("Stok tersedia: ${product.stock} pcs", 14f, muted))

        content.addView(sectionTitle("Informasi Produk"), margins(-1, -2, 0, dp(24), 0, dp(8)))
        content.addView(text("Kategori", 13f, muted))
        content.addView(text(product.category, 16f, textColor(), true))

        content.addView(text("Harga Beli", 13f, muted, false, Gravity.NO_GRAVITY),
            margins(-1, -2, 0, dp(14), 0, 0))
        content.addView(text(rupiah(product.buyPrice), 16f, textColor(), true))

        content.addView(text("Deskripsi", 13f, muted),
            margins(-1, -2, 0, dp(14), 0, 0))
        content.addView(text(product.description.ifBlank { "-" }, 15f, textColor()))

        val delete = Button(this)
        delete.text = "Hapus Produk"
        delete.setTextColor(Color.WHITE)
        delete.background = rounded(Color.rgb(205, 55, 55), 14)
        delete.setOnClickListener {
            products.remove(product)
            saveState()
            showProducts()
        }

        content.addView(delete, margins(-1, dp(52), 0, dp(28), 0, 0))

        root.addView(content)
        setContentView(root)
    }

    private fun showAddProduct() {
        setBack { showProducts() }
        val root = LinearLayout(this)
        root.orientation = LinearLayout.VERTICAL
        root.background = backgroundColor()

        root.addView(topBar("Tambah Produk") { showProducts() })

        val scroll = ScrollView(this)
        val content = LinearLayout(this)
        content.orientation = LinearLayout.VERTICAL
        content.setPadding(dp(20), dp(18), dp(20), dp(20))

        val name = input("Nama Produk")
        val category = input("Kategori (Makanan / Minuman)")
        val buy = input("Harga Beli", true)
        val sell = input("Harga Jual", true)
        val stock = input("Stok", true)
        val desc = input("Deskripsi Produk")

        content.addView(name)
        content.addView(category, margins(-1, dp(54), 0, dp(10), 0, 0))
        content.addView(buy, margins(-1, dp(54), 0, dp(10), 0, 0))
        content.addView(sell, margins(-1, dp(54), 0, dp(10), 0, 0))
        content.addView(stock, margins(-1, dp(54), 0, dp(10), 0, 0))
        content.addView(desc, margins(-1, dp(54), 0, dp(10), 0, 0))

        val save = button("Simpan Produk")
        save.setOnClickListener {
            if (name.text.toString().trim().isEmpty()) {
                toast("Nama produk wajib diisi")
                return@setOnClickListener
            }

            products.add(
                Product(
                    name.text.toString().trim(),
                    sell.text.toString().toIntOrNull() ?: 0,
                    buy.text.toString().toIntOrNull() ?: 0,
                    stock.text.toString().toIntOrNull() ?: 0,
                    category.text.toString().ifBlank { "Produk" },
                    desc.text.toString()
                )
            )

            saveState()
            toast("Produk berhasil ditambahkan")
            showProducts()
        }

        content.addView(save, margins(-1, dp(52), 0, dp(18), 0, 0))

        scroll.addView(content)
        root.addView(scroll, LinearLayout.LayoutParams(-1, 0, 1f))

        setContentView(root)
    }

    private fun showCalculator() {
        setBack { showDashboard() }
        val root = LinearLayout(this)
        root.orientation = LinearLayout.VERTICAL
        root.background = backgroundColor()

        root.addView(topBar("Kalkulator Harga Jual") { showDashboard() })

        val content = LinearLayout(this)
        content.orientation = LinearLayout.VERTICAL
        content.setPadding(dp(20), dp(18), dp(20), dp(20))

        content.addView(sectionTitle("Metode Markup"))

        val hpp = input("Harga Pokok (HPP)", true)
        val additional = input("Biaya Tambahan", true)
        val margin = input("Persentase Markup (%)", true)

        val result = text("Harga Jual\nRp 0", 24f, green, true, Gravity.CENTER)
        result.background = rounded(softGreen, 18)
        result.setPadding(dp(16), dp(20), dp(16), dp(20))

        val calculate = button("Hitung")

        calculate.setOnClickListener {
            val modal = hpp.text.toString().toDoubleOrNull() ?: 0.0
            val biaya = additional.text.toString().toDoubleOrNull() ?: 0.0
            val persen = margin.text.toString().toDoubleOrNull() ?: 0.0

            val totalModal = modal + biaya
            val price = totalModal + (totalModal * persen / 100.0)

            result.text = "Harga Jual Rekomendasi\n${rupiah(price.toInt())}"
        }

        content.addView(hpp, margins(-1, dp(54), 0, dp(16), 0, 0))
        content.addView(additional, margins(-1, dp(54), 0, dp(10), 0, 0))
        content.addView(margin, margins(-1, dp(54), 0, dp(10), 0, 0))
        content.addView(calculate, margins(-1, dp(52), 0, dp(16), 0, 0))
        content.addView(result)

        root.addView(content)
        root.addView(Space(this), LinearLayout.LayoutParams(-1, 0, 1f))
        root.addView(bottomNav("Kalkulator"))

        setContentView(root)
    }

    private fun showSaleForm() {
        setBack { showDashboard() }
        val root = LinearLayout(this)
        root.orientation = LinearLayout.VERTICAL
        root.background = backgroundColor()

        root.addView(topBar("Catat Penjualan") { showDashboard() })

        val content = LinearLayout(this)
        content.orientation = LinearLayout.VERTICAL
        content.setPadding(dp(20), dp(20), dp(20), dp(20))

        val product = input("Nama Produk")
        val quantity = input("Jumlah", true)
        val price = input("Harga Satuan", true)

        val total = text("Total Penjualan\nRp 0", 21f, green, true)
        total.background = rounded(softGreen, 16)
        total.setPadding(dp(16), dp(16), dp(16), dp(16))

        val save = button("Simpan Penjualan")
        save.setOnClickListener {
            val qty = quantity.text.toString().toIntOrNull() ?: 0
            val p = price.text.toString().toIntOrNull() ?: 0
            val amount = qty * p

            total.text = "Total Penjualan\n${rupiah(amount)}"

            transactions.add(
                0,
                Sale(
                    "${product.text.toString().ifBlank { "Penjualan" }} • $qty pcs",
                    amount,
                    "Penjualan"
                )
            )

            saveState()
            toast("Penjualan berhasil disimpan")
        }

        content.addView(product)
        content.addView(quantity, margins(-1, dp(54), 0, dp(10), 0, 0))
        content.addView(price, margins(-1, dp(54), 0, dp(10), 0, 0))
        content.addView(total, margins(-1, -2, 0, dp(14), 0, 0))
        content.addView(save, margins(-1, dp(52), 0, dp(14), 0, 0))

        root.addView(content)
        setContentView(root)
    }

    private fun showExpense() {
        setBack { showDashboard() }
        val root = LinearLayout(this)
        root.orientation = LinearLayout.VERTICAL
        root.background = backgroundColor()

        root.addView(topBar("Catat Pengeluaran") { showDashboard() })

        val content = LinearLayout(this)
        content.orientation = LinearLayout.VERTICAL
        content.setPadding(dp(20), dp(20), dp(20), dp(20))

        val category = input("Kategori")
        val amount = input("Jumlah Pengeluaran", true)
        val note = input("Keterangan")

        val save = button("Simpan")
        save.setOnClickListener {
            val value = amount.text.toString().toIntOrNull() ?: 0

            transactions.add(
                0,
                Sale(
                    "${category.text.toString().ifBlank { "Pengeluaran" }} - ${note.text}",
                    value,
                    "Pengeluaran"
                )
            )

            saveState()
            toast("Pengeluaran berhasil disimpan")
            showDashboard()
        }

        content.addView(category)
        content.addView(amount, margins(-1, dp(54), 0, dp(10), 0, 0))
        content.addView(note, margins(-1, dp(54), 0, dp(10), 0, 0))
        content.addView(save, margins(-1, dp(52), 0, dp(16), 0, 0))

        root.addView(content)
        setContentView(root)
    }

    private fun showReport() {
        setBack { showDashboard() }
        val revenue = transactions
            .filter { it.type == "Penjualan" }
            .sumOf { it.amount }

        val expense = transactions
            .filter { it.type == "Pengeluaran" }
            .sumOf { it.amount }

        val profit = revenue - expense

        val root = LinearLayout(this)
        root.orientation = LinearLayout.VERTICAL
        root.background = backgroundColor()

        root.addView(topBar("Laporan Usaha") { showDashboard() })

        val content = LinearLayout(this)
        content.orientation = LinearLayout.VERTICAL
        content.setPadding(dp(18), dp(18), dp(18), dp(18))

        content.addView(reportCard("Total Penjualan", rupiah(revenue), green))
        content.addView(reportCard("Total Pengeluaran", rupiah(expense), Color.rgb(210, 70, 60)),
            margins(-1, -2, 0, dp(10), 0, 0))
        content.addView(reportCard("Keuntungan Bersih", rupiah(profit), green),
            margins(-1, -2, 0, dp(10), 0, 0))

        content.addView(sectionTitle("Grafik Penjualan"),
            margins(-1, -2, 0, dp(24), 0, dp(12)))

        val chart = LinearLayout(this)
        chart.gravity = Gravity.BOTTOM
        chart.orientation = LinearLayout.HORIZONTAL
        chart.setPadding(dp(12), dp(20), dp(12), dp(12))
        chart.background = rounded(cardColor(), 18)

        listOf(90, 140, 110, 170, 210, 160, 190).forEach {
            val bar = View(this)
            bar.background = rounded(green, 6)
            chart.addView(bar, weightMargins(1f, dp(5), dp(220 - it), dp(5), 0))
        }

        content.addView(chart, lp(-1, dp(250)))

        root.addView(content)
        root.addView(Space(this), LinearLayout.LayoutParams(-1, 0, 1f))
        root.addView(bottomNav("Laporan"))

        setContentView(root)
    }

    private fun showTransactions() {
        setBack { showDashboard() }
        val root = LinearLayout(this)
        root.orientation = LinearLayout.VERTICAL
        root.background = backgroundColor()

        root.addView(topBar("Transaksi") { showDashboard() })

        val scroll = ScrollView(this)
        val content = LinearLayout(this)
        content.orientation = LinearLayout.VERTICAL
        content.setPadding(dp(16), dp(16), dp(16), dp(16))

        transactions.forEach {
            content.addView(
                transactionCard(it),
                margins(-1, -2, 0, 0, 0, dp(8))
            )
        }

        scroll.addView(content)
        root.addView(scroll, LinearLayout.LayoutParams(-1, 0, 1f))
        root.addView(bottomNav("Transaksi"))

        setContentView(root)
    }

    private fun showSettings() {
        setBack { showDashboard() }
        val root = LinearLayout(this)
        root.orientation = LinearLayout.VERTICAL
        root.background = backgroundColor()

        root.addView(topBar("Pengaturan") { showDashboard() })

        val content = LinearLayout(this)
        content.orientation = LinearLayout.VERTICAL
        content.setPadding(dp(16), dp(16), dp(16), dp(16))

        content.addView(settingCard("Data Usaha", "Nama usaha dan informasi toko") { showBusinessData() }, margins(-1, -2, 0, 0, 0, dp(8)))
        content.addView(settingCard("Backup & Restore", "Backup data dan pulihkan kapan saja") { showBackupRestore() }, margins(-1, -2, 0, 0, 0, dp(8)))
        content.addView(settingCard("Notifikasi", if (notificationsEnabled) "Aktif • Pengingat stok rendah" else "Nonaktif") { showNotificationSettings() }, margins(-1, -2, 0, 0, 0, dp(8)))

        val dark = settingCard(
            "Tema",
            if (darkMode) "Dark Mode aktif" else "Light Mode aktif"
        ) {
            darkMode = !darkMode
            saveState()
            showSettings()
        }

        content.addView(dark)

        content.addView(
            settingCard("Tentang Aplikasi", "VECTA HUB UMKM • VECTA PROJECT") {},
            margins(-1, -2, 0, dp(8), 0, 0)
        )

        root.addView(content)
        root.addView(Space(this), LinearLayout.LayoutParams(-1, 0, 1f))
        root.addView(bottomNav("Lainnya"))

        setContentView(root)
    }

    private fun showBusinessData() {
        setBack { showSettings() }
        val root = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setBackgroundColor(backgroundColor()) }
        root.addView(topBar("Data Usaha") { showSettings() })
        val scroll = ScrollView(this)
        val content = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setPadding(dp(20), dp(20), dp(20), dp(24)) }
        val name = input("Nama Usaha").apply { setText(businessName) }
        val owner = input("Nama Pemilik").apply { setText(businessOwner) }
        val phone = input("Nomor WhatsApp / Telepon").apply { setText(businessPhone); inputType = InputType.TYPE_CLASS_PHONE }
        val address = input("Alamat Usaha").apply { setText(businessAddress); minLines = 2; maxLines = 4; setSingleLine(false) }
        content.addView(name); content.addView(owner, margins(-1, dp(60), 0, dp(12), 0, 0)); content.addView(phone, margins(-1, dp(60), 0, dp(12), 0, 0)); content.addView(address, margins(-1, dp(92), 0, dp(12), 0, 0))
        val save = button("Simpan Data Usaha")
        save.setOnClickListener {
            if (name.text.toString().trim().isBlank()) { toast("Nama usaha wajib diisi"); return@setOnClickListener }
            businessName = name.text.toString().trim(); businessOwner = owner.text.toString().trim(); businessPhone = phone.text.toString().trim(); businessAddress = address.text.toString().trim()
            saveState(); toast("Data usaha berhasil disimpan"); showSettings()
        }
        content.addView(save, margins(-1, dp(54), 0, dp(18), 0, 0)); scroll.addView(content); root.addView(scroll, LinearLayout.LayoutParams(-1, 0, 1f)); setContentView(root)
    }

    private fun showNotificationSettings() {
        setBack { showSettings() }
        val root = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setBackgroundColor(backgroundColor()) }
        root.addView(topBar("Notifikasi") { showSettings() })
        val content = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setPadding(dp(20), dp(20), dp(20), dp(20)) }
        val row = LinearLayout(this).apply { gravity = Gravity.CENTER_VERTICAL; setPadding(dp(16), dp(14), dp(16), dp(14)); background = rounded(cardColor(), 18) }
        val info = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        info.addView(text("Notifikasi Aktif", 17f, textColor(), true)); info.addView(text("Aktifkan pengingat stok rendah", 13f, muted))
        val sw = Switch(this).apply { isChecked = notificationsEnabled }
        row.addView(info, LinearLayout.LayoutParams(0, -2, 1f)); row.addView(sw); content.addView(row)
        content.addView(text("Batas stok rendah", 15f, textColor(), true), margins(-1, -2, 0, dp(18), 0, dp(8)))
        val threshold = input("Contoh: 5", true).apply { setText(lowStockThreshold.toString()) }
        content.addView(threshold, lp(-1, dp(60)))
        val save = button("Simpan Pengaturan Notifikasi")
        save.setOnClickListener { notificationsEnabled = sw.isChecked; lowStockThreshold = (threshold.text.toString().toIntOrNull() ?: 5).coerceAtLeast(0); saveState(); toast("Pengaturan notifikasi disimpan") }
        content.addView(save, margins(-1, dp(54), 0, dp(16), 0, 0))
        val test = button("Cek Stok Rendah")
        test.setOnClickListener { val low = products.filter { it.stock <= lowStockThreshold }; toast(if (low.isEmpty()) "Tidak ada produk dengan stok rendah" else "Stok rendah: " + low.joinToString { it.name }) }
        content.addView(test, margins(-1, dp(54), 0, dp(10), 0, 0)); root.addView(content); root.addView(Space(this), LinearLayout.LayoutParams(-1, 0, 1f)); setContentView(root)
    }

    private fun showBackupRestore() {
        setBack { showSettings() }
        val root = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setBackgroundColor(backgroundColor()) }
        root.addView(topBar("Backup & Restore") { showSettings() })
        val content = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setPadding(dp(20), dp(20), dp(20), dp(20)) }
        content.addView(text("Backup Data", 20f, textColor(), true)); content.addView(text("Simpan salinan produk, transaksi, dan pengaturan dalam format teks JSON.", 14f, muted))
        val backup = button("Buat & Salin Backup")
        backup.setOnClickListener {
            val json = exportStateJson(); val cm = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager; cm.setPrimaryClip(ClipData.newPlainText("Backup UMKM Smart", json)); toast("Backup berhasil dibuat dan disalin ke clipboard")
        }
        content.addView(backup, margins(-1, dp(54), 0, dp(16), 0, 0))
        content.addView(text("Restore Data", 20f, textColor(), true)); content.addView(text("Tempel teks backup yang sebelumnya disalin.", 14f, muted))
        val restore = button("Restore dari Teks Backup")
        restore.setOnClickListener { showRestoreDialog() }
        content.addView(restore, margins(-1, dp(54), 0, dp(12), 0, 0))
        val reset = button("Reset Data ke Contoh")
        reset.setOnClickListener { AlertDialog.Builder(this).setTitle("Reset data?").setMessage("Produk dan transaksi akan kembali ke data contoh.").setNegativeButton("Batal", null).setPositiveButton("Reset") { _, _ -> resetSampleData(); saveState(); toast("Data berhasil direset"); showSettings() }.show() }
        content.addView(reset, margins(-1, dp(54), 0, dp(12), 0, 0)); root.addView(content); root.addView(Space(this), LinearLayout.LayoutParams(-1, 0, 1f)); setContentView(root)
    }

    private fun showRestoreDialog() {
        val input = EditText(this).apply { hint = "Tempel teks JSON backup di sini"; minLines = 6; gravity = Gravity.TOP; setPadding(dp(16), dp(12), dp(16), dp(12)) }
        AlertDialog.Builder(this).setTitle("Restore Backup").setView(input).setNegativeButton("Batal", null).setPositiveButton("Pulihkan") { _, _ ->
            try { importStateJson(input.text.toString()); saveState(); toast("Data berhasil dipulihkan"); showSettings() } catch (e: Exception) { toast("Backup tidak valid") }
        }.show()
    }

    private fun exportStateJson(): String {
        val root = JSONObject(); root.put("businessName", businessName); root.put("businessOwner", businessOwner); root.put("businessPhone", businessPhone); root.put("businessAddress", businessAddress); root.put("darkMode", darkMode); root.put("notificationsEnabled", notificationsEnabled); root.put("lowStockThreshold", lowStockThreshold)
        val ps = JSONArray(); products.forEach { p -> ps.put(JSONObject().put("name", p.name).put("sellPrice", p.sellPrice).put("buyPrice", p.buyPrice).put("stock", p.stock).put("category", p.category).put("description", p.description)) }; root.put("products", ps)
        val ts = JSONArray(); transactions.forEach { t -> ts.put(JSONObject().put("title", t.title).put("amount", t.amount).put("type", t.type)) }; root.put("transactions", ts); return root.toString()
    }

    private fun importStateJson(raw: String) {
        val root = JSONObject(raw); businessName = root.optString("businessName", businessName); businessOwner = root.optString("businessOwner", businessOwner); businessPhone = root.optString("businessPhone", businessPhone); businessAddress = root.optString("businessAddress", businessAddress); darkMode = root.optBoolean("darkMode", darkMode); notificationsEnabled = root.optBoolean("notificationsEnabled", notificationsEnabled); lowStockThreshold = root.optInt("lowStockThreshold", lowStockThreshold)
        val ps = root.optJSONArray("products") ?: JSONArray(); val ts = root.optJSONArray("transactions") ?: JSONArray(); products.clear(); for (i in 0 until ps.length()) { val o = ps.getJSONObject(i); products.add(Product(o.optString("name"), o.optInt("sellPrice"), o.optInt("buyPrice"), o.optInt("stock"), o.optString("category", "Produk"), o.optString("description"))) }
        transactions.clear(); for (i in 0 until ts.length()) { val o = ts.getJSONObject(i); transactions.add(Sale(o.optString("title"), o.optInt("amount"), o.optString("type"))) }
    }

    private fun saveState() { prefs.edit().putString("state", exportStateJson()).apply() }
    private fun loadState() { val raw = prefs.getString("state", null) ?: return; try { importStateJson(raw) } catch (_: Exception) {} }
    private fun resetSampleData() { products.clear(); products.addAll(listOf(Product("Keripik Singkong",5000,3000,20,"Makanan","Keripik singkong renyah dan gurih"), Product("Pisang Crispy",8000,4500,15,"Makanan","Pisang crispy dengan topping pilihan"), Product("Brownies",12000,7000,8,"Makanan","Brownies cokelat lembut"), Product("Es Teh Manis",3000,1200,30,"Minuman","Teh manis segar"), Product("Kopi Susu",7000,3500,5,"Minuman","Kopi susu creamy"))); transactions.clear(); transactions.addAll(listOf(Sale("Keripik Singkong • 5 pcs",25000,"Penjualan"),Sale("Kopi Susu • 3 pcs",21000,"Penjualan"),Sale("Pisang Crispy • 2 pcs",16000,"Penjualan"),Sale("Pembelian bahan baku",50000,"Pengeluaran"))) }

    private fun topBar(title: String, back: () -> Unit): View {
        val bar = LinearLayout(this)
        bar.gravity = Gravity.CENTER_VERTICAL
        bar.setPadding(dp(12), dp(8), dp(16), dp(8))
        bar.background = darkGreen

        val backButton = Button(this)
        backButton.text = "←"
        backButton.textSize = 22f
        backButton.setTextColor(Color.WHITE)
        backButton.background = Color.TRANSPARENT
        backButton.setOnClickListener { back() }

        val titleText = text(title, 18f, Color.WHITE, true)

        bar.addView(backButton, lp(dp(54), dp(52)))
        bar.addView(titleText, LinearLayout.LayoutParams(0, dp(52), 1f))

        return bar
    }

    private fun bottomNav(active: String): View {
        val nav = LinearLayout(this)
        nav.orientation = LinearLayout.HORIZONTAL
        nav.gravity = Gravity.CENTER
        nav.setPadding(dp(4), dp(4), dp(4), dp(4))
        nav.background = cardColor()

        val items = listOf(
            "Beranda" to { showDashboard() },
            "Kasir" to { showCashier() },
            "Produk" to { showProducts() },
            "Laporan" to { showReport() },
            "Lainnya" to { showSettings() }
        )

        items.forEach { (name, action) ->
            val b = Button(this)
            b.text = name
            b.textSize = 10f
            b.setTextColor(if (name == active) green else muted)
            b.background = Color.TRANSPARENT
            b.setOnClickListener { action() }
            nav.addView(b, LinearLayout.LayoutParams(0, dp(54), 1f))
        }

        return nav
    }

    private fun addMenuGrid(
        parent: LinearLayout,
        icon1: String,
        title1: String,
        action1: () -> Unit,
        icon2: String,
        title2: String,
        action2: () -> Unit
    ) {
        val row = LinearLayout(this)
        row.orientation = LinearLayout.HORIZONTAL

        row.addView(
            menuCard(icon1, title1, action1),
            weightMargins(1f, 0, 0, dp(5), 0)
        )

        row.addView(
            menuCard(icon2, title2, action2),
            weightMargins(1f, dp(5), 0, 0, 0)
        )

        parent.addView(row, margins(-1, -2, 0, 0, 0, dp(10)))
    }

    private fun menuCard(icon: String, title: String, action: () -> Unit): View {
        val card = LinearLayout(this)
        card.orientation = LinearLayout.VERTICAL
        card.gravity = Gravity.CENTER
        card.setPadding(dp(10), dp(14), dp(10), dp(14))
        card.background = rounded(cardColor(), 18)

        card.addView(text(icon, 25f, green, false, Gravity.CENTER))
        card.addView(text(title, 12f, textColor(), true, Gravity.CENTER))

        card.setOnClickListener { action() }

        return card
    }

    private fun statCard(title: String, value: String, subtitle: String): View {
        val card = LinearLayout(this)
        card.orientation = LinearLayout.VERTICAL
        card.setPadding(dp(18), dp(16), dp(18), dp(16))
        card.background = rounded(cardColor(), 20)

        card.addView(text(title, 13f, muted))
        card.addView(text(value, 24f, textColor(), true))
        card.addView(text(subtitle, 12f, green))

        return card
    }

    private fun smallStat(title: String, value: String): View {
        val card = LinearLayout(this)
        card.orientation = LinearLayout.VERTICAL
        card.setPadding(dp(14), dp(14), dp(14), dp(14))
        card.background = rounded(cardColor(), 18)

        card.addView(text(title, 12f, muted))
        card.addView(text(value, 15f, textColor(), true))

        return card
    }

    private fun reportCard(title: String, value: String, color: Int): View {
        val card = LinearLayout(this)
        card.orientation = LinearLayout.VERTICAL
        card.setPadding(dp(16), dp(16), dp(16), dp(16))
        card.background = rounded(cardColor(), 18)

        card.addView(text(title, 13f, muted))
        card.addView(text(value, 21f, color, true))

        return card
    }

    private fun transactionCard(item: Sale): View {
        val row = LinearLayout(this)
        row.orientation = LinearLayout.HORIZONTAL
        row.gravity = Gravity.CENTER_VERTICAL
        row.setPadding(dp(14), dp(13), dp(14), dp(13))
        row.background = rounded(cardColor(), 16)

        val info = LinearLayout(this)
        info.orientation = LinearLayout.VERTICAL

        info.addView(text(item.type, 12f, muted))
        info.addView(text(item.title, 15f, textColor(), true))

        val color = if (item.type == "Penjualan") green else Color.rgb(210, 70, 60)
        val amount = text(rupiah(item.amount), 14f, color, true, Gravity.END)

        row.addView(info, LinearLayout.LayoutParams(0, -2, 1f))
        row.addView(amount)

        return row
    }

    private fun settingCard(title: String, subtitle: String, action: () -> Unit): View {
        val row = LinearLayout(this)
        row.orientation = LinearLayout.VERTICAL
        row.setPadding(dp(16), dp(15), dp(16), dp(15))
        row.background = rounded(cardColor(), 18)

        row.addView(text(title, 16f, textColor(), true))
        row.addView(text(subtitle, 12f, muted))

        row.setOnClickListener { action() }

        return row
    }

    private fun sectionTitle(value: String): TextView {
        return text(value, 19f, textColor(), true)
    }

    private fun input(hint: String, numeric: Boolean = false): EditText {
        val e = EditText(this)
        e.hint = hint
        e.textSize = 17f
        e.minHeight = dp(58)
        e.setSingleLine(!hint.contains("Deskripsi"))
        e.setTextColor(textColor())
        e.setHintTextColor(muted)
        e.background = rounded(inputColor(), 14)
        e.setPadding(dp(14), 0, dp(14), 0)

        if (numeric) {
            e.inputType = InputType.TYPE_CLASS_NUMBER
        }

        return e
    }

    private fun button(label: String): Button {
        val b = Button(this)
        b.text = label
        b.textSize = 14f
        b.setTextColor(Color.WHITE)
        b.isAllCaps = false
        b.background = rounded(green, 14)
        return b
    }

    private fun text(
        value: String,
        size: Float,
        color: Int,
        bold: Boolean = false,
        gravity: Int = Gravity.NO_GRAVITY
    ): TextView {
        val t = TextView(this)
        t.text = value
        t.textSize = size
        t.setTextColor(color)
        t.gravity = gravity

        if (bold) {
            t.setTypeface(Typeface.DEFAULT_BOLD)
        }

        return t
    }

    private fun rounded(color: Int, radius: Int): GradientDrawable {
        return GradientDrawable().apply {
            setColor(color)
            cornerRadius = dp(radius).toFloat()
        }
    }

    private fun backgroundColor(): Int {
        return if (darkMode) Color.rgb(15, 25, 23) else bgLight
    }

    private fun cardColor(): Int {
        return if (darkMode) Color.rgb(28, 42, 38) else Color.WHITE
    }

    private fun inputColor(): Int {
        return if (darkMode) Color.rgb(38, 55, 50) else Color.rgb(242, 246, 244)
    }

    private fun textColor(): Int {
        return if (darkMode) Color.WHITE else textDark
    }

    private fun rupiah(value: Int): String {
        return NumberFormat
            .getCurrencyInstance(Locale("id", "ID"))
            .format(value)
            .replace(",00", "")
    }

    private fun toast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun dp(value: Int): Int {
        return (value * resources.displayMetrics.density).toInt()
    }

    private fun lp(width: Int, height: Int): LinearLayout.LayoutParams {
        return LinearLayout.LayoutParams(width, height)
    }

    private fun margins(
        width: Int,
        height: Int,
        left: Int,
        top: Int,
        right: Int,
        bottom: Int
    ): LinearLayout.LayoutParams {
        return LinearLayout.LayoutParams(width, height).apply {
            setMargins(left, top, right, bottom)
        }
    }

    private fun weightMargins(
        weight: Float,
        left: Int,
        top: Int,
        right: Int,
        bottom: Int
    ): LinearLayout.LayoutParams {
        return LinearLayout.LayoutParams(0, -2, weight).apply {
            setMargins(left, top, right, bottom)
        }
    }
}
