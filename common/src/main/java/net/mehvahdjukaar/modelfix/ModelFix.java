package net.mehvahdjukaar.modelfix;

import com.mojang.math.Vector3f;
import net.minecraft.client.renderer.block.model.BlockElement;
import net.minecraft.client.renderer.block.model.ItemModelGenerator;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

public class ModelFix {
    public static final String MOD_ID = "modelfix";

    public static float getShrinkRatio() {
        return 0;
    }

    public static float getRecess() {
        return 0.006f;// 0.0045f;//0.019f;//0.0055f;
    }

    public static float getExpansion() {
        return 0.006f;//0.013f;//0.008f;//0.011f;
    }

    public static void createOrExpandSpan(List<ItemModelGenerator.Span> listSpans, ItemModelGenerator.SpanFacing spanFacing,
                                          int pixelX, int pixelY) {
        int length;
        ItemModelGenerator.Span existingSpan = null;
        for (ItemModelGenerator.Span span2 : listSpans) {
            if (span2.getFacing() == spanFacing) {
                int i = spanFacing.isHorizontal() ? pixelY : pixelX;
                if (span2.getAnchor() != i) continue;
                //skips faces with transparent pixels so we can enlarge safely
                if (span2.getMax() != (!spanFacing.isHorizontal() ? pixelY : pixelX) - 1) continue;
                existingSpan = span2;
                break;
            }
        }

        length = spanFacing.isHorizontal() ? pixelX : pixelY;
        if (existingSpan == null) {
            int newStart = spanFacing.isHorizontal() ? pixelY : pixelX;
            listSpans.add(new ItemModelGenerator.Span(spanFacing, length, newStart));
        } else {
            existingSpan.expand(length);
        }
    }

    public static void enlargeFaces(CallbackInfoReturnable<List<BlockElement>> cir) {
        float inc = ModelFix.getRecess();
        float inc2 = ModelFix.getExpansion();
        for (var e : cir.getReturnValue()) {
            Vector3f from = e.from;
            Vector3f to = e.to;

            var set = e.faces.keySet();
            if (set.size() == 1) {
                var dir = set.stream().findAny().get();
                switch (dir) {
                    case UP -> {
                        from.set(from.x() - inc2, from.y() - inc, from.z() - inc2);
                        to.set(to.x() + inc2, to.y() - inc, to.z() + inc2);
                    }
                    case DOWN -> {
                        from.set(from.x()- inc2, from.y() + inc, from.z() - inc2);
                        to.set(to.x()+inc2, to.y() + inc, to.z() + inc2);
                    }
                    case WEST -> {
                        from.set(from.x() - inc, from.y() + inc2, from.z() - inc2);
                        to.set(to.x() - inc, to.y() - inc2, to.z() + inc2);
                    }
                    case EAST -> {
                        from.set(from.x() + inc, from.y()+ inc2, from.z() - inc2);
                        to.set(to.x() + inc, to.y()- inc2, to.z() + inc2);
                    }
                }
            }
        }
    }
}