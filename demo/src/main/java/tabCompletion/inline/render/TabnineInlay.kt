package tabCompletion.inline.render

import com.intellij.openapi.Disposable
import com.intellij.openapi.editor.Editor
import tabCompletion.prediction.TabNineCompletion
import java.awt.Rectangle

interface TabnineInlay : Disposable {
    val offset: Int?
    val isEmpty: Boolean

    fun getBounds(): Rectangle?
    fun render(editor: Editor, completion: TabNineCompletion, offset: Int)

    companion object {
        @JvmStatic
        fun create(parent: Disposable): TabnineInlay {
            return DefaultTabnineInlay(parent)
        }
    }
}
