package com.seongokryu.relocationplanner.data.export

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Environment
import com.seongokryu.relocationplanner.domain.model.Task
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDate
import java.time.format.DateTimeFormatter

object PdfExporter {
    private const val PAGE_WIDTH = 595
    private const val PAGE_HEIGHT = 842
    private const val MARGIN = 40f
    private const val LINE_HEIGHT = 18f

    fun exportToPdf(
        context: Context,
        tasks: List<Task>,
    ): File {
        val document = PdfDocument()
        val stats = ExportFormatter.calculateExportStats(tasks)
        val categoryGroups = ExportFormatter.groupByCategory(tasks)

        var pageNumber = 1
        var pageInfo = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, pageNumber).create()
        var page = document.startPage(pageInfo)
        var canvas = page.canvas
        var yPos = MARGIN

        val titlePaint = createPaint(size = 20f, bold = true)
        val subtitlePaint = createPaint(size = 14f, bold = true)
        val bodyPaint = createPaint(size = 11f)
        val donePaint = createPaint(size = 11f, color = Color.GRAY)
        val headerPaint = createPaint(size = 12f, bold = true, color = Color.rgb(33, 150, 243))

        // Title
        canvas.drawText("렛츠고 USA — 이주 체크리스트", MARGIN, yPos, titlePaint)
        yPos += LINE_HEIGHT * 1.5f

        // Date
        val today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
        canvas.drawText("작성일: $today", MARGIN, yPos, bodyPaint)
        yPos += LINE_HEIGHT * 1.5f

        // Stats
        canvas.drawText(
            "전체 진행률: ${stats.done}/${stats.total} (${stats.progressPercent}%)",
            MARGIN,
            yPos,
            subtitlePaint,
        )
        yPos += LINE_HEIGHT * 2f

        // Categories
        for (group in categoryGroups) {
            // Check if we need a new page
            if (yPos > PAGE_HEIGHT - MARGIN - LINE_HEIGHT * 3) {
                document.finishPage(page)
                pageNumber++
                pageInfo = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, pageNumber).create()
                page = document.startPage(pageInfo)
                canvas = page.canvas
                yPos = MARGIN
            }

            // Category header
            val headerText =
                "${group.category.icon} ${group.category.label} (${group.done}/${group.total})"
            canvas.drawText(headerText, MARGIN, yPos, headerPaint)
            yPos += LINE_HEIGHT * 1.2f

            drawProgressBar(canvas, MARGIN, yPos, group.done, group.total)
            yPos += LINE_HEIGHT

            // Tasks in category
            for (task in group.tasks) {
                if (yPos > PAGE_HEIGHT - MARGIN) {
                    document.finishPage(page)
                    pageNumber++
                    pageInfo =
                        PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, pageNumber).create()
                    page = document.startPage(pageInfo)
                    canvas = page.canvas
                    yPos = MARGIN
                }

                val line = ExportFormatter.formatTaskForExport(task)
                val paint = if (task.isDone) donePaint else bodyPaint
                canvas.drawText(line, MARGIN + 16f, yPos, paint)
                yPos += LINE_HEIGHT
            }

            yPos += LINE_HEIGHT * 0.5f
        }

        document.finishPage(page)

        val file =
            File(
                context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS),
                "relocation_checklist_$today.pdf",
            )
        FileOutputStream(file).use { document.writeTo(it) }
        document.close()

        return file
    }

    private fun createPaint(
        size: Float,
        bold: Boolean = false,
        color: Int = Color.BLACK,
    ): Paint =
        Paint().apply {
            textSize = size
            this.color = color
            isAntiAlias = true
            if (bold) isFakeBoldText = true
        }

    private fun drawProgressBar(
        canvas: Canvas,
        x: Float,
        y: Float,
        done: Int,
        total: Int,
    ) {
        val barWidth = 200f
        val barHeight = 8f
        val progress = if (total > 0) done.toFloat() / total else 0f

        val bgPaint = Paint().apply { color = Color.LTGRAY }
        val fgPaint = Paint().apply { color = Color.rgb(76, 175, 80) }

        canvas.drawRect(x, y - barHeight, x + barWidth, y, bgPaint)
        canvas.drawRect(x, y - barHeight, x + barWidth * progress, y, fgPaint)
    }
}
