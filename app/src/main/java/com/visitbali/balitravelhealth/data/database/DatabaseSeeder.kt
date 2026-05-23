package com.visitbali.balitravelhealth.data.database

import com.visitbali.balitravelhealth.data.model.FacilityType
import com.visitbali.balitravelhealth.data.model.HealthcareFacility

/**
 * Seed data — 17 BMTA-listed healthcare facilities in Bali.
 *
 * ── Sumber verifikasi data ──────────────────────────────────────────────────
 * Alamat / Telp:
 *   • Website resmi masing-masing RS
 *   • Australian Embassy Bali Medical List (bali.indonesia.embassy.gov.au)
 *   • US Embassy Medical Assistance Indonesia (id.usembassy.gov)
 *   • SIRS Kemkes (sirs.kemkes.go.id)
 *
 * Jam Operasional:
 *   • profngoerahhospitalbali.com  → Poli: Sen–Jum 07.30–16.00
 *   • rsmatabalimandara.baliprov.go.id / sipp.menpan.go.id → Loket 07.30–11.30
 *   • rsbm.baliprov.go.id/fqa & balimandarahospital.com → Poli detail verified
 *   • rstrijata.com → IGD 24 jam confirmed
 *   • rs.unud.ac.id/pelayanan-rawat-jalan → Poli: Sen–Sabtu (jam per site)
 *   • rsudmangusada.badungkab.go.id → IGD confirmed; poli jam kerja
 *   • siloamhospitals.com & @siloambali Instagram → IGD & poli hours
 *   • bimcbali.com (Kuta & Nusa Dua) → 24 jam confirmed
 *   • primamedika.com → IGD 24 jam; poli standard
 *   • balimedicalcare.com (BROS) → 24 jam confirmed
 *   • kih.co.id → Hotline & poli hours
 *   • rsmramata.com → Standard clinic hours
 *   • bali911dentalclinic.com → Sen–Sab 10.00–19.30; Emergency 24/7
 *   • 221assist.com → 24/7 confirmed
 *
 * ── Catatan jam ─────────────────────────────────────────────────────────────
 *   ⚠️  = jam diestimasi / tidak dikonfirmasi secara eksplisit dari sumber resmi
 *   Jam poliklinik dapat berubah sewaktu-waktu — selalu konfirmasi via telepon/website.
 *
 * ── Koordinat ───────────────────────────────────────────────────────────────
 *   Approksimasi ±100–200 m. Validasi dengan Google Maps Places API.
 *
 * Last verified: Mei 2026
 */
object DatabaseSeeder {

    fun getAllFacilities(): List<HealthcareFacility> = listOf(

        // ════════════════════════════════════════════════════════════════════
        // RS PEMERINTAH
        // ════════════════════════════════════════════════════════════════════

        HealthcareFacility(
            name = "Prof. Ngoerah Hospital",
            officialName = "RSUP Prof. Dr. I.G.N.G. Ngoerah",
            specialty = "Heart Care / Kardiologi",
            type = FacilityType.GOVERNMENT,
            address = "Jl. Diponegoro No. 45, Dauh Puri Klod, Denpasar, Bali 80113",
            phone = "+62 361 227911",
            phoneAlt = "+62 361 227915",
            website = "https://profngoerahhospitalbali.com",
            email = "info@profngoerahhospitalbali.com",
            latitude = -8.6684,
            longitude = 115.2190,
            // Sumber: profngoerahhospitalbali.com/home/pelayanan-rawat-jalan-umum/
            isOpen24Hours = true,
            outpatientHours = "Senin – Jumat: 07.30 – 16.00 WITA\n" +
                    "Sabtu & Libur Nasional: Tutup (Poli Umum)\n" +
                    "Wing Amerta (Eksekutif): Senin – Jumat sesi pagi & sore\n" +
                    "Loket pendaftaran: Senin–Kamis 07.00–13.30, Jumat 07.00–13.00",
            emergencyHours = "IGD: 24 jam / 7 hari",
            hoursSummary = "IGD 24 jam  |  Poli: Sen–Jum 07.30–16.00",
            notes = "Rujukan utama Bali. Dulu RS Sanglah. International Wing, 738 TT, 500+ dokter. " +
                    "Lab & Radiologi penunjang 24 jam. Hiperbarik, onkologi, transplantasi ginjal."
        ),

        HealthcareFacility(
            name = "RS Mata Bali Mandara",
            officialName = "Rumah Sakit Mata Bali Mandara",
            specialty = "Eye Care / Mata",
            type = FacilityType.GOVERNMENT,
            address = "Jl. Angsoka No. 8, Dangin Puri Kangin, Denpasar Utara, Bali 80236",
            phone = "+62 361 243350",
            website = "https://rsmatabalimandara.baliprov.go.id",
            latitude = -8.6389,
            longitude = 115.2228,
            // Sumber: sipp.menpan.go.id & rsmatabalimandara.baliprov.go.id
            isOpen24Hours = true,
            outpatientHours = "Loket Poliklinik: 07.30 – 11.30 WITA (Senin – Jumat)\n" +
                    "Poli VIP: Sesi pagi & sore (dengan perjanjian)\n" +
                    "Sabtu: ⚠️ Terbatas / dengan perjanjian",
            emergencyHours = "IGD Mata: 24 jam / 7 hari",
            hoursSummary = "IGD 24 jam  |  Loket Poli: 07.30–11.30 WITA",
            notes = "RS khusus mata Pemprov Bali Tipe A. Katarak, retina, glaukoma, strabismus, LASIK."
        ),

        HealthcareFacility(
            name = "RSUD Bali Mandara",
            officialName = "Rumah Sakit Umum Daerah Bali Mandara Provinsi Bali",
            specialty = "Cancer / Onkologi Terpadu",
            type = FacilityType.GOVERNMENT,
            address = "Jl. Bypass Ngurah Rai No. 548, Sanur Kauh, Denpasar Selatan, Bali 80234",
            phone = "+62 361 4490566",
            phoneAlt = "+62 812 3712 0596",
            website = "https://rsbm.baliprov.go.id",
            email = "marketingbalimandarahospital@gmail.com",
            latitude = -8.7228,
            longitude = 115.2410,
            // Sumber: rsbm.baliprov.go.id/fqa (resmi, detail)
            isOpen24Hours = true,
            outpatientHours = "Poli Pagi:\n" +
                    "  Senin – Kamis: 08.00 – 15.30 WITA\n" +
                    "  Jumat & Sabtu: 08.00 – 13.00 WITA\n" +
                    "Poli Sore:\n" +
                    "  Senin – Jumat: 15.30 – 20.00 WITA\n" +
                    "Pendaftaran online: rsbm.baliprov.go.id | WA: 0812-3712-0596",
            emergencyHours = "IGD: 24 jam / 7 hari",
            hoursSummary = "IGD 24 jam  |  Poli Pagi: Sen–Sab  |  Poli Sore: Sen–Jum",
            notes = "RS daerah Bali kelas B. Layanan kanker terpadu. IGD 24 jam, radiologi, rehabilitasi. BPJS & asuransi."
        ),

        HealthcareFacility(
            name = "RS Bhayangkara Denpasar",
            officialName = "Rumah Sakit Bhayangkara Denpasar (RS Trijata)",
            specialty = "Hyperbaric Medicine / Oksigen Hiperbarik",
            type = FacilityType.GOVERNMENT,
            address = "Jl. Trijata No. 32, Sumerta Kelod, Denpasar Utara, Bali",
            phone = "+62 361 4723350",
            website = "https://rstrijata.com",
            latitude = -8.6473,
            longitude = 115.2299,
            // Sumber: rstrijata.com — IGD 24 jam dikonfirmasi; jam poli = standar RS Polri
            isOpen24Hours = true,
            outpatientHours = "Poliklinik Spesialis:\n" +
                    "  ⚠️ Senin – Jumat: 08.00 – 14.00 WITA (estimasi jam kerja)\n" +
                    "  Sabtu: ⚠️ Terbatas (cek langsung ke RS)\n" +
                    "Poli Hiperbarik: Sesuai jadwal dokter (konfirmasi via telp)\n" +
                    "Home Care: Tersedia",
            emergencyHours = "IGD: 24 jam / 7 hari",
            hoursSummary = "IGD 24 jam  |  Poli: ⚠️ Sen–Jum 08.00–14.00 (estimasi)",
            notes = "RS Polri kelas C. Hiperbarik tersertifikasi DAN. Luka diabetes, dekompresi selam, luka bakar. 109 TT."
        ),

        HealthcareFacility(
            name = "RS Universitas Udayana",
            officialName = "Rumah Sakit Universitas Udayana",
            specialty = "Infectious Diseases / Penyakit Infeksi & Tropis",
            type = FacilityType.GOVERNMENT,
            address = "Jl. Rumah Sakit Universitas Udayana No. 1, Jimbaran, Kuta Selatan, Badung, Bali 80361",
            phone = "+62 361 8953670",
            phoneAlt = "+62 896 0490 0890",
            website = "https://rs.unud.ac.id",
            email = "info@rs.unud.ac.id",
            latitude = -8.7897,
            longitude = 115.1688,
            // Sumber: rs.unud.ac.id/pelayanan-rawat-jalan ("Poliklinik Spesialis Buka Senin s/d...")
            // Detail jam tidak diterbitkan lengkap; menggunakan standar RS pendidikan Bali
            isOpen24Hours = true,
            outpatientHours = "Poliklinik Spesialis:\n" +
                    "  ⚠️ Senin – Sabtu: 08.00 – 15.00 WITA (konfirmasi via rs.unud.ac.id)\n" +
                    "Reservasi Poli: +62 831 5958 2772\n" +
                    "Informasi: +62 896 0490 0890",
            emergencyHours = "IGD: 24 jam / 7 hari",
            hoursSummary = "IGD 24 jam  |  Poli: ⚠️ Sen–Sab (konfirmasi via website)",
            notes = "RS perguruan tinggi. Penyakit infeksi & tropis, imunologi, operasi telerobotik. Dekat kampus UNUD Jimbaran."
        ),

        HealthcareFacility(
            name = "RSUD Mangusada Badung",
            officialName = "Rumah Sakit Daerah Mangusada Kabupaten Badung",
            specialty = "Heart & Cancer / Jantung & Onkologi",
            type = FacilityType.GOVERNMENT,
            address = "Jl. Raya Kapal, Mangupura, Mengwi, Badung, Bali 80351",
            phone = "+62 361 9006812",
            phoneAlt = "+62 361 9006813",
            website = "https://rsudmangusada.badungkab.go.id",
            email = "rsdm@rsdmangusada.com",
            latitude = -8.5867,
            longitude = 115.1811,
            // Sumber: Instagram @rsdmangusada (IGD 24 jam, WA jam kerja)
            // Jam poli: standar RS daerah kelas B Bali
            isOpen24Hours = true,
            outpatientHours = "Poliklinik:\n" +
                    "  ⚠️ Senin – Kamis: 08.00 – 14.00 WITA\n" +
                    "  Jumat: 08.00 – 11.00 WITA\n" +
                    "  Sabtu: ⚠️ Terbatas / sebagian poli saja\n" +
                    "WhatsApp (jam kerja): 087850127333\n" +
                    "IGD: +62 361 9006811",
            emergencyHours = "IGD: 24 jam / 7 hari  |  Tel IGD: (0361) 9006811",
            hoursSummary = "IGD 24 jam  |  Poli: ⚠️ Sen–Jum (jam kerja)",
            notes = "RS Kabupaten Badung kelas B. IGD 24 jam, ICU, bedah jantung, kemoterapi, hemodialisis. BPJS & asuransi."
        ),

        // ════════════════════════════════════════════════════════════════════
        // RS SWASTA
        // ════════════════════════════════════════════════════════════════════

        HealthcareFacility(
            name = "Siloam Hospital Bali",
            officialName = "Siloam Hospitals Bali (Denpasar)",
            specialty = "Orthopedics / Ortopedi",
            type = FacilityType.PRIVATE,
            address = "Jl. Sunset Road No. 818, Kuta, Badung, Bali 80361",
            phone = "+62 361 779900",
            phoneAlt = "1-500-911",
            website = "https://www.siloamhospitals.com/en/rumah-sakit/siloam-hospitals-denpasar",
            email = "info.bali@siloamhospitals.com",
            latitude = -8.7109,
            longitude = 115.1705,
            // Sumber: @siloambali Instagram — Emergency & Contact Center 1-500-911 (24/7)
            // Jam poli: standar Siloam (berbasis siloamhospitals.com clinic hours)
            isOpen24Hours = true,
            outpatientHours = "Rawat Jalan / Poliklinik:\n" +
                    "  ⚠️ Senin – Jumat: 08.00 – 20.00 WITA\n" +
                    "  Sabtu: 08.00 – 17.00 WITA\n" +
                    "  Minggu: Sesuai jadwal dokter\n" +
                    "Contact Center: 1-500-911 (24 jam)",
            emergencyHours = "IGD: 24 jam / 7 hari  |  Emergency: 1-500-911",
            hoursSummary = "IGD 24 jam  |  Poli: ⚠️ Sen–Jum 08.00–20.00",
            notes = "Terakreditasi KARS. 124 TT. Ortopedi, trauma, hemodialisis, kemoterapi, NICU. Staf Inggris. Area Kuta."
        ),

        HealthcareFacility(
            name = "BIMC Hospital Nusa Dua",
            officialName = "BIMC Siloam Hospital Nusa Dua",
            specialty = "Cosmetics / Estetika & Bedah Kosmetik",
            type = FacilityType.PRIVATE,
            address = "Kawasan ITDC Blok D, Jl. Nusa Dua, Benoa, Kuta Selatan, Badung, Bali 80363",
            phone = "+62 361 3000911",
            phoneAlt = "+62 811 3896 113",
            website = "https://bimcbali.com",
            email = "info@bimcbali.com",
            latitude = -8.8009,
            longitude = 115.2285,
            // Sumber: bimcbali.com — 24 jam A&E & Medical Centre confirmed
            isOpen24Hours = true,
            outpatientHours = "Accident & Emergency Centre: 24 jam / 7 hari\n" +
                    "Medical Centre (Konsultasi, MCU, Hotel Visit): 24 jam / 7 hari\n" +
                    "CosMedic Centre (Bedah Estetik & Kosmetik):\n" +
                    "  ⚠️ Senin – Jumat: 09.00 – 17.00 WITA\n" +
                    "Dental Centre: ⚠️ Sesuai jadwal dokter\n" +
                    "Dialysis Centre: Sesuai jadwal terapi",
            emergencyHours = "A&E: 24 jam / 7 hari  |  Tel: +62 361 3000911",
            hoursSummary = "24 jam / 7 hari (A&E & Medical Centre)",
            notes = "Akreditasi ACHSI Australia. CosMedic, dental, dialisis, emergency 24 jam. Dalam kompleks ITDC, dekat hotel bintang 5."
        ),

        HealthcareFacility(
            name = "BIMC Hospital Kuta",
            officialName = "BIMC Hospital Kuta",
            specialty = "Emergencies / IGD & Gawat Darurat",
            type = FacilityType.PRIVATE,
            address = "Jl. Bypass Ngurah Rai No. 100X, Kuta, Bali 80361",
            phone = "+62 361 761263",
            phoneAlt = "+62 811 3960 8500",
            website = "https://bimcbali.com",
            email = "info@bimcbali.com",
            latitude = -8.7237,
            longitude = 115.1774,
            // Sumber: bimcbali.com/bimc-hospital-kuta — 24 jam A&E & Medical Centre confirmed
            isOpen24Hours = true,
            outpatientHours = "Accident & Emergency Centre: 24 jam / 7 hari\n" +
                    "Medical Centre (Konsultasi, MCU, on-call hotel): 24 jam / 7 hari\n" +
                    "Laboratorium & Farmasi: 24 jam / 7 hari\n" +
                    "Radiologi: 24 jam / 7 hari\n" +
                    "WA Appointment: +62 811 3960 8500",
            emergencyHours = "A&E: 24 jam / 7 hari  |  Tel: +62 361 761263",
            hoursSummary = "24 jam / 7 hari (semua layanan)",
            notes = "Cabang terbesar BIMC. Emergency & trauma 24 jam, ICU, Lab, Radiologi. Rujukan utama wisatawan Kuta/Seminyak. 90+ mitra asuransi."
        ),

        HealthcareFacility(
            name = "Prima Medika Hospital",
            officialName = "Rumah Sakit Umum Prima Medika",
            specialty = "Cancer / Onkologi",
            type = FacilityType.PRIVATE,
            address = "Jl. Pulau Serangan No. 9X, Denpasar, Bali 80232",
            phone = "+62 361 236225",
            website = "https://www.primamedika.com",
            email = "rspmmail@gmail.com",
            latitude = -8.6896,
            longitude = 115.2140,
            // Sumber: primamedika.com & flip.id (IGD 24 jam dikonfirmasi)
            isOpen24Hours = true,
            outpatientHours = "Rawat Jalan / Poliklinik Spesialis:\n" +
                    "  ⚠️ Senin – Sabtu: 08.00 – 20.00 WITA\n" +
                    "  Minggu: Sesuai jadwal dokter\n" +
                    "Home Clinic: Tersedia\n" +
                    "Medical Coordination (pasien internasional): Sesuai jam kerja",
            emergencyHours = "IGD: 24 jam / 7 hari",
            hoursSummary = "IGD 24 jam  |  Poli: ⚠️ Sen–Sab 08.00–20.00",
            notes = "100 TT, 30+ spesialisasi. Medical coordination untuk pasien internasional. Staf Inggris. Onkologi, neuro, fisiologi."
        ),

        HealthcareFacility(
            name = "Bali Royal Hospital",
            officialName = "BROS — Bali Royal Hospital",
            specialty = "IVF & Plastic Surgery / Fertilisasi & Bedah Plastik",
            type = FacilityType.PRIVATE,
            address = "Jl. Letda Tantular No. 6, Renon, Denpasar Timur, Kota Denpasar, Bali 80234",
            phone = "+62 361 222588",
            website = "https://balimedicalcare.com",
            latitude = -8.6678,
            longitude = 115.2342,
            // Sumber: flip.id ("24-hour services") & balimedicalcare.com
            isOpen24Hours = true,
            outpatientHours = "Rawat Jalan & Spesialis:\n" +
                    "  ⚠️ Senin – Sabtu: 08.00 – 20.00 WITA\n" +
                    "  Minggu: Sesuai jadwal dokter\n" +
                    "MCU (Medical Check Up): Senin – Sabtu\n" +
                    "Unit Bersalin / Maternity: 24 jam",
            emergencyHours = "IGD & Bersalin: 24 jam / 7 hari",
            hoursSummary = "IGD 24 jam  |  Poli: ⚠️ Sen–Sab 08.00–20.00",
            notes = "Beroperasi Juli 2010. IVF, bedah plastik, neurologi, pediatri, kardiologi. Di kawasan civic center Renon."
        ),

        HealthcareFacility(
            name = "Kasih Ibu Hospital Saba",
            officialName = "Rumah Sakit Umum Kasih Ibu Saba",
            specialty = "Hyperbaric Surgery / Hiperbarik & Kedokteran Selam",
            type = FacilityType.PRIVATE,
            address = "Jl. Raya Pantai Saba No. 9, Saba, Blahbatuh, Gianyar, Bali 80581",
            phone = "+62 811 398 3030",
            phoneAlt = "+62 811 380 5356",
            website = "https://kih.co.id/our-hospital/kasih-ibu-hospital-saba/",
            email = "care@kasihibuhospital.com",
            latitude = -8.6051,
            longitude = 115.3135,
            // Sumber: kih.co.id — Hotline (0361) 3003030 group-wide 24 jam
            isOpen24Hours = true,
            outpatientHours = "Poliklinik & Rawat Jalan:\n" +
                    "  ⚠️ Senin – Sabtu: 08.00 – 20.00 WITA\n" +
                    "  Minggu: Sesuai jadwal dokter\n" +
                    "Hiperbarik (HDMC): Jadwal sesuai indikasi medis (konfirmasi via telp)\n" +
                    "Hemodialisis: Sesuai jadwal sesi\n" +
                    "Booking: WA +62 811 398 3030",
            emergencyHours = "IGD & Ambulans: 24 jam / 7 hari  |  Tel: +62 811 380 5356",
            hoursSummary = "IGD 24 jam  |  Poli: ⚠️ Sen–Sab 08.00–20.00",
            notes = "150 TT. Berdiri 2016. RS terlengkap Bali Timur. Hiperbarik (HDMC), neuro, ortopedi. ~45 mnt Denpasar, ~30 mnt Ubud. Staf Inggris & Jepang."
        ),

        HealthcareFacility(
            name = "Kasih Ibu Hospital Denpasar",
            officialName = "Rumah Sakit Umum Kasih Ibu Denpasar",
            specialty = "Neurosurgery / Bedah Saraf",
            type = FacilityType.PRIVATE,
            address = "Jl. Teuku Umar No. 120, Dauh Puri Klod, Denpasar Barat, Bali 80114",
            phone = "+62 361 3003030",
            phoneAlt = "+62 361 223036",
            website = "https://kih.co.id/our-hospital/kasih-ibu-hospital-denpasar/",
            email = "care@kasihibuhospital.com",
            latitude = -8.6674,
            longitude = 115.2070,
            // Sumber: kih.co.id — hotline 0361-3003030, Polyclinic Admission 0361-3004141
            isOpen24Hours = true,
            outpatientHours = "Poliklinik / Rawat Jalan:\n" +
                    "  ⚠️ Senin – Sabtu: 08.00 – 20.00 WITA\n" +
                    "  Minggu: Sesuai jadwal dokter\n" +
                    "Poli Admission: (0361) 3004141\n" +
                    "International Division: Senin – Sabtu (jam kerja)\n" +
                    "Hemodialisis & MCU: Sesuai jadwal\n" +
                    "WA Booking: +62 811 398 3030",
            emergencyHours = "IGD & Ambulans: 24 jam / 7 hari  |  Hotline: (0361) 3003030",
            hoursSummary = "IGD 24 jam  |  Poli: ⚠️ Sen–Sab 08.00–20.00",
            notes = "RS induk KIH. Pertama CT & MRI di Bali. EMRAM Level 6. International division untuk wisatawan. Poli Admission: (0361) 3004141."
        ),

        HealthcareFacility(
            name = "RS Mata Ramata",
            officialName = "Rumah Sakit Khusus Mata Ramata",
            specialty = "Eye Care / Oftalmologi Komprehensif",
            type = FacilityType.PRIVATE,
            address = "Jl. Gatot Subroto Barat No. 429, Padangsambian Kaja, Denpasar Barat, Bali 80117",
            phone = "+62 361 429429",
            website = "https://rsmramata.com",
            latitude = -8.6400,
            longitude = 115.1900,
            // Sumber: rsmramata.com — jam tidak diterbitkan eksplisit; booking via WA dikonfirmasi
            // Estimasi berdasarkan RS khusus mata kelas C di Bali
            isOpen24Hours = true,
            outpatientHours = "Poliklinik Mata:\n" +
                    "  ⚠️ Senin – Sabtu: 08.00 – 20.00 WITA\n" +
                    "  Minggu: ⚠️ Sesuai kebutuhan / on-call\n" +
                    "Booking via WhatsApp: Tersedia (bahasa Inggris & Indonesia)\n" +
                    "Bedah Elektif (Katarak, LASIK, dll): Jadwal spesifik sesuai dokter",
            emergencyHours = "IGD Mata: 24 jam / 7 hari (on-call dokter spesialis)",
            hoursSummary = "IGD Mata 24 jam  |  Poli: ⚠️ Sen–Sab 08.00–20.00",
            notes = "RS mata swasta pertama Provinsi Bali. 20+ dokter spesialis mata. Katarak, retina, LASIK, strabismus. Kelas C."
        ),

        // ════════════════════════════════════════════════════════════════════
        // KLINIK
        // ════════════════════════════════════════════════════════════════════

        HealthcareFacility(
            name = "Bali 911 Dental Clinic",
            officialName = "Bali 911 Dental Clinic — Implant Centre",
            specialty = "Dental Care / Gigi & Mulut",
            type = FacilityType.CLINIC,
            address = "Jl. Gatot Subroto Barat No. 367, Pemecutan Kaja, Denpasar Utara, Bali 80116",
            phone = "+62 812 3800 911",
            phoneAlt = "+62 838 9972 6765",
            website = "https://bali911dentalclinic.com",
            latitude = -8.6432,
            longitude = 115.1980,
            // Sumber: bali911dentalclinic.com — jam resmi dikonfirmasi
            isOpen24Hours = false,
            outpatientHours = "Senin – Jumat: 10.00 – 19.30 WITA\n" +
                    "Sabtu: 10.00 – 18.30 WITA\n" +
                    "Minggu & Hari Libur: Tutup (kecuali emergency)\n" +
                    "WA Konsultasi (Sen–Jum): 10.30 – 20.30 WITA\n" +
                    "WA Konsultasi (Sabtu): 10.30 – 19.00 WITA\n" +
                    "\nCabang Kuta (Mall Bali Galeria Lt. 2): Jam menyesuaikan mal\n" +
                    "Cabang Kuta Sunset (Jl. Sunsetroad Indah 1 Kav. 7): Sesuai jadwal",
            emergencyHours = "Emergency Gigi (on-call): 24 jam / 7 hari  |  Tel/WA: +62 812 3800 911",
            hoursSummary = "Emergency 24 jam  |  Klinik: Sen–Jum 10.00–19.30, Sab 10.00–18.30",
            notes = "30+ thn pengalaman. Implan, Invisalign, crown, bridge, veneer. Lab on-site (hasil 2–7 hari). 2 cabang di Kuta."
        ),

        HealthcareFacility(
            name = "Penta Medika Clinic",
            officialName = "Klinik Penta Medika",
            specialty = "Medical Evacuation / Evakuasi Medis",
            type = FacilityType.CLINIC,
            address = "Jl. Teuku Umar Barat (Marlboro) No. 88, Denpasar, Bali",
            phone = "+62 361 490709",
            phoneAlt = "+62 361 7446144",
            latitude = -8.6639,
            longitude = 115.1992,
            // Sumber: Australian Embassy Bali Medical List
            // Sebagai layanan evakuasi medis, on-call 24 jam adalah standar industri
            isOpen24Hours = true,
            outpatientHours = "Layanan Klinis & Konsultasi:\n" +
                    "  ⚠️ Senin – Jumat: 08.00 – 17.00 WITA (jam kerja)\n" +
                    "  Sabtu: ⚠️ Terbatas (konfirmasi via telp)\n" +
                    "Evakuasi Medis & Dokter On-call:\n" +
                    "  24 jam / 7 hari (by appointment / emergency)\n" +
                    "Manajemen asuransi internasional: Jam kerja",
            emergencyHours = "Medical Evacuation On-call: 24 jam / 7 hari",
            hoursSummary = "Evakuasi 24 jam  |  Klinik: ⚠️ Sen–Jum 08.00–17.00",
            notes = "Klinik evakuasi medis berlisensi. Evakuasi darat/udara, dokter on-call, manajemen pasien asuransi internasional. Bilingual Inggris/Indonesia."
        ),

        HealthcareFacility(
            name = "221 Assist Clinic",
            officialName = "221 Assist — Medical Assistance & Evacuation",
            specialty = "Medical Evacuation / Evakuasi & Repatriasi",
            type = FacilityType.CLINIC,
            address = "Jl. Anyelir No. 8, Denpasar, Bali",
            phone = "+62 815 5822 1221",
            website = "https://www.221assist.com",
            email = "service@221assist.com",
            latitude = -8.6560,
            longitude = 115.2200,
            // Sumber: 221assist.com — layanan 24 jam dikonfirmasi dari berbagai halaman
            isOpen24Hours = true,
            outpatientHours = "Semua layanan: 24 jam / 7 hari\n" +
                    "  • Medivac darat / udara / laut\n" +
                    "  • Medical & non-medical escort penerbangan\n" +
                    "  • Repatriasi jenazah\n" +
                    "  • Doctor home visit 24 jam\n" +
                    "  • Klinik industri & standby medis event\n" +
                    "Kantor (konsultasi tatap muka): ⚠️ Jam kerja",
            emergencyHours = "On-call 24 jam / 7 hari  |  Tel: +62 815 5822 1221\n" +
                    "Email: service@221assist.com",
            hoursSummary = "24 jam / 7 hari (semua layanan evakuasi)",
            notes = "Berdiri 2009. Medivac darat/udara/laut, repatriasi jenazah, dokter home visit 24 jam. Beroperasi di seluruh Indonesia. Berbahasa Inggris."
        )
    )
}
