package com.rocketnotfound.rnf.forge.mixin.client;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.model.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xaero.common.minimap.render.radar.EntityIconDefinitions;
import xaero.common.minimap.render.radar.EntityIconModelPartsRenderer;
import xaero.common.minimap.render.radar.EntityIconPrerenderer;
import xaero.common.minimap.render.radar.ModelRenderDetectionElement;
import xaero.common.minimap.render.radar.resource.EntityIconModelConfig;
import xaero.common.misc.Misc;

import java.util.*;

@Pseudo
@Mixin(EntityIconPrerenderer.class)
public class MixinXaerosMinimapHeadPartsCheck {
    @Shadow
    private EntityIconModelPartsRenderer entityModelPartsRenderer;

    @Shadow
    private Object resolveModelRoot(EntityModel<?> model, ArrayList<ArrayList<String>> rootPath, Entity entity) {
        throw new Error("Mixin did not apply!");
    }

    @Shadow
    private VertexConsumer setupModelRenderType(VertexConsumerProvider.Immediate renderTypeBuffer, Identifier entityTexture, ModelRenderDetectionElement mrde) {
        throw new Error("Mixin did not apply!");
    }

    @Shadow
    public ModelPart searchSuperclassFields(VertexConsumer vertexBuilder, Object modelRoot, ArrayList<ModelPart> renderedModels, ModelPart mainPart, List<String> filter, boolean justOne, boolean zeroRotation, ModelRenderDetectionElement mrde) {
        throw new Error("Mixin did not apply!");
    }

    @Inject(
        method = "Lxaero/common/minimap/render/radar/EntityIconPrerenderer;renderModel(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider$Immediate;Lnet/minecraft/client/render/entity/EntityRenderer;Lnet/minecraft/client/render/entity/model/EntityModel;Lnet/minecraft/entity/Entity;Lxaero/common/minimap/render/radar/resource/EntityIconModelConfig;Lxaero/common/minimap/render/radar/resource/EntityIconModelConfig;Lnet/minecraft/client/model/ModelPart;Ljava/util/ArrayList;Lnet/minecraft/util/Identifier;ZZLxaero/common/minimap/render/radar/ModelRenderDetectionElement;Ljava/util/List;Ljava/util/List;)Lnet/minecraft/client/model/ModelPart;",
        at = @At("HEAD"),
        cancellable = true
    )
    private void renderModelHeadPartNullCheckFix(MatrixStack matrixStack, VertexConsumerProvider.Immediate renderTypeBuffer, EntityRenderer entityRenderer, EntityModel entityModel, Entity entity, EntityIconModelConfig modelConfig, EntityIconModelConfig defaultModelConfig, ModelPart mainPart, ArrayList<ModelPart> renderedModels, Identifier entityTexture, boolean forceFieldCheck, boolean fullModelIcon, ModelRenderDetectionElement mrde, List<String> hardcodedMainPartAliases, List<String> hardcodedModelPartsFields, CallbackInfoReturnable<ModelPart> cir) {
        boolean isChildBU = entityModel.child;
        entityModel.child = false;
        if (entityTexture != null) {
            Object modelRoot = null;
            if (modelConfig.modelRootPath != null) {
                modelRoot = resolveModelRoot(entityModel, modelConfig.modelRootPath, entity);
            }

            if (modelRoot == null) {
                modelRoot = EntityIconDefinitions.getModelRoot(entityRenderer, entityModel);
            }

            boolean treatAsHierarchicalRoot = false;
            ModelPart rootPart;
            if (modelConfig == defaultModelConfig && modelRoot instanceof AnimalModel && !(modelRoot instanceof BipedEntityModel)) {
                Iterable<ModelPart> headPartsTest = (Iterable) Misc.getReflectMethodValue(modelRoot, entityModelPartsRenderer.ageableModelHeadPartsMethod, new Object[0]);
                if (headPartsTest != null) {
                    Iterator<ModelPart> iterator = headPartsTest.iterator();
                    if (iterator.hasNext() && (rootPart = (ModelPart) iterator.next()) != null && !iterator.hasNext() && !entityModelPartsRenderer.hasDirectCubes(rootPart)) {
                        modelRoot = rootPart;
                        treatAsHierarchicalRoot = true;
                        fullModelIcon = true;
                    }
                }
            }

            VertexConsumer vertexBuilder = setupModelRenderType(renderTypeBuffer, entityTexture, mrde);
            if (modelConfig.modelMainPartFieldAliases != null && !modelConfig.modelMainPartFieldAliases.isEmpty()) {
                mainPart = searchSuperclassFields(vertexBuilder, modelRoot, renderedModels, mainPart, modelConfig.modelMainPartFieldAliases, true, modelConfig.modelPartsRotationReset, mrde);
            }

            if (!forceFieldCheck && modelRoot instanceof AnimalModel) {
                if (modelRoot instanceof BipedEntityModel) {
                    ModelPart headRenderer = ((BipedEntityModel)modelRoot).head;
                    if (mainPart == null) {
                        mainPart = headRenderer;
                    }

                    rootPart = ((BipedEntityModel)modelRoot).hat;
                    entityModelPartsRenderer.renderPart(matrixStack, vertexBuilder, headRenderer, renderedModels, mainPart, modelConfig.modelPartsRotationReset, mrde);
                    entityModelPartsRenderer.renderPart(matrixStack, vertexBuilder, rootPart, renderedModels, mainPart, modelConfig.modelPartsRotationReset, mrde);
                }

                mainPart = entityModelPartsRenderer.renderDeclaredMethod(matrixStack, vertexBuilder, entityModelPartsRenderer.ageableModelHeadPartsMethod, (AnimalModel)modelRoot, renderedModels, mainPart, modelConfig.modelPartsRotationReset, mrde);
                if (fullModelIcon) {
                    mainPart = entityModelPartsRenderer.renderDeclaredMethod(matrixStack, vertexBuilder, entityModelPartsRenderer.ageableModelBodyPartsMethod, (AnimalModel)modelRoot, renderedModels, mainPart, modelConfig.modelPartsRotationReset, mrde);
                }
            } else {
                boolean singlePartSucceeded = false;
                if (!forceFieldCheck && (treatAsHierarchicalRoot || modelRoot instanceof SinglePartEntityModel)) {
                    if (treatAsHierarchicalRoot) {
                        rootPart = (ModelPart)modelRoot;
                    } else {
                        SinglePartEntityModel singlePartModel = (SinglePartEntityModel)modelRoot;
                        rootPart = singlePartModel.getPart();
                    }

                    if (rootPart != null) {
                        ModelPart headPart;
                        try {
                            headPart = rootPart.getChild("head");
                        } catch (NoSuchElementException var24) {
                            headPart = null;
                        }

                        if (headPart != null) {
                            if (mainPart == null) {
                                mainPart = headPart;
                            }

                            entityModelPartsRenderer.renderPart(matrixStack, vertexBuilder, headPart, renderedModels, mainPart, modelConfig.modelPartsRotationReset, mrde);
                            singlePartSucceeded = true;
                        }

                        if (fullModelIcon) {
                            Map<String, ModelPart> rootChildren = entityModelPartsRenderer.getChildModels(rootPart);
                            mainPart = entityModelPartsRenderer.renderPartsIterable(rootChildren.values(), matrixStack, vertexBuilder, renderedModels, mainPart, modelConfig.modelPartsRotationReset, mrde);
                            singlePartSucceeded = true;
                        }
                    }
                }

                if (!singlePartSucceeded) {
                    if (!forceFieldCheck && modelRoot instanceof CompositeEntityModel && fullModelIcon) {
                        mainPart = entityModelPartsRenderer.renderDeclaredMethod(matrixStack, vertexBuilder, entityModelPartsRenderer.segmentedModelPartsMethod, (CompositeEntityModel)modelRoot, renderedModels, mainPart, modelConfig.modelPartsRotationReset, mrde);
                    } else {
                        if (!forceFieldCheck && modelRoot instanceof ModelWithHead) {
                            rootPart = ((ModelWithHead)modelRoot).getHead();
                            if (mainPart == null) {
                                mainPart = rootPart;
                            }

                            entityModelPartsRenderer.renderPart(matrixStack, vertexBuilder, rootPart, renderedModels, mainPart, modelConfig.modelPartsRotationReset, mrde);
                        }

                        if (modelConfig.modelPartsFields == null) {
                            mainPart = searchSuperclassFields(vertexBuilder, modelRoot, renderedModels, mainPart, hardcodedMainPartAliases, true, modelConfig.modelPartsRotationReset, mrde);
                        }

                        List<String> headPartsFields = hardcodedModelPartsFields;
                        if (fullModelIcon) {
                            headPartsFields = null;
                        } else if (modelConfig.modelPartsFields != null) {
                            headPartsFields = modelConfig.modelPartsFields;
                        }

                        mainPart = searchSuperclassFields(vertexBuilder, modelRoot, renderedModels, mainPart, (List)headPartsFields, false, modelConfig.modelPartsRotationReset, mrde);
                    }
                }
            }

            renderTypeBuffer.draw();
        }

        entityModel.child = isChildBU;

        cir.setReturnValue(mainPart);
    }
}
