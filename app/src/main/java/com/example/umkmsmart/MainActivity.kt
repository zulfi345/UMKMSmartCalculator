package com.example.umkmsmart

import android.app.Activity
import android.app.AlertDialog
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.InputType
import android.view.Gravity
import android.view.View
import android.widget.*
import java.text.NumberFormat
import java.util.Locale

class MainActivity : Activity() {

    private val green = Color.rgb(7, 117, 70)
    private val darkGreen = Color.rgb(4, 79, 48)
    private val softGreen = Color.rgb(229, 246, 237)
    private val bgLight = Color.rgb(247, 249, 248)
    private val textDark = Color.rgb(28, 47, 42)
    private val muted = Color.rgb(105, 120, 115)

    private var darkMode = false

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
        showSplash()
    }

    private fun showSplash() {
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
        card.addView(username, margins(-1, dp(54), 0, 0, 0, dp(12)))
        card.addView(password, margins(-1, dp(54), 0, 0, 0, dp(16)))
        card.addView(login, lp(-1, dp(52)))
        card.addView(register, margins(-1, -2, 0, dp(18), 0, 0))

        root.addView(logo, lp(dp(150), dp(150)))
        root.addView(card, margins(-1, -2, 0, dp(16), 0, 0))

        setContentView(root)
    }

    private fun showDashboard() {
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
        val root = LinearLayout(this)
        root.orientation = LinearLayout.VERTICAL
        root.background = backgroundColor()

        root.addView(topBar("Kasir") { showDashboard() })

        val search = EditText(this)
        search.hint = "Cari produk..."
        search.background = rounded(inputColor(), 14)
        search.setPadding(dp(14), 0, dp(14), 0)

        root.addView(search, margins(-1, dp(52), dp(16), dp(12), dp(16), dp(8)))

        val category = LinearLayout(this)
        category.orientation = LinearLayout.HORIZONTAL
        category.setPadding(dp(16), 0, dp(16), dp(8))

        listOf("Semua", "Makanan", "Minuman").forEachIndexed { index, name ->
            val b = Button(this)
            b.text = name
            b.textSize = 11f
            b.setTextColor(if (index == 0) Color.WHITE else textColor())
            b.background = rounded(if (index == 0) green else inputColor(), 14)
            category.addView(b, weightMargins(1f, if (index == 0) 0 else dp(4), 0, 0, 0))
        }

        root.addView(category)

        val scroll = ScrollView(this)
        val list = LinearLayout(this)
        list.orientation = LinearLayout.VERTICAL
        list.setPadding(dp(16), dp(4), dp(16), dp(16))

        products.forEach { product ->
            list.addView(
                cashierProduct(product),
                margins(-1, -2, 0, 0, 0, dp(8))
            )
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

        val icon = text(
            if (product.category == "Minuman") "🥤" else "🍟",
            26f,
            textColor(),
            false,
            Gravity.CENTER
        )

        val info = LinearLayout(this)
        info.orientation = LinearLayout.VERTICAL

        info.addView(text(product.name, 16f, textColor(), true))
        info.addView(text("${rupiah(product.sellPrice)}   •   Stok: ${product.stock}", 12f, muted))

        val add = Button(this)
        add.text = "+"
        add.textSize = 20f
        add.setTextColor(Color.WHITE)
        add.background = rounded(green, 50)

        add.setOnClickListener {
            if (product.stock <= 0) {
                toast("Stok produk habis")
            } else {
                cart[product.name] = (cart[product.name] ?: 0) + 1
                showCashier()
            }
        }

        row.addView(icon, lp(dp(48), dp(48)))
        row.addView(info, LinearLayout.LayoutParams(0, -2, 1f))
        row.addView(add, lp(dp(48), dp(48)))

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
        toast("Pembayaran berhasil disimpan")
        showDashboard()
    }

    private fun showProducts() {
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
            showProducts()
        }

        content.addView(delete, margins(-1, dp(52), 0, dp(28), 0, 0))

        root.addView(content)
        setContentView(root)
    }

    private fun showAddProduct() {
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

            toast("Produk berhasil ditambahkan")
            showProducts()
        }

        content.addView(save, margins(-1, dp(52), 0, dp(18), 0, 0))

        scroll.addView(content)
        root.addView(scroll, LinearLayout.LayoutParams(-1, 0, 1f))

        setContentView(root)
    }

    private fun showCalculator() {
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
        val root = LinearLayout(this)
        root.orientation = LinearLayout.VERTICAL
        root.background = backgroundColor()

        root.addView(topBar("Pengaturan") { showDashboard() })

        val content = LinearLayout(this)
        content.orientation = LinearLayout.VERTICAL
        content.setPadding(dp(16), dp(16), dp(16), dp(16))

        listOf(
            "Data Usaha" to "Nama usaha dan informasi toko",
            "Backup & Restore" to "Simpan dan pulihkan data",
            "Notifikasi" to "Pengingat stok dan transaksi"
        ).forEach { item ->
            content.addView(
                settingCard(item.first, item.second) {},
                margins(-1, -2, 0, 0, 0, dp(8))
            )
        }

        val dark = settingCard(
            "Tema",
            if (darkMode) "Dark Mode aktif" else "Light Mode aktif"
        ) {
            darkMode = !darkMode
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
        e.textSize = 14f
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
