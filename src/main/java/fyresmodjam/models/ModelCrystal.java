package fyresmodjam.models;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelCrystal extends ModelBase {
   ModelRenderer CrystalMain;
   ModelRenderer CrystalEdge1;
   ModelRenderer CrystalEdge2;
   ModelRenderer CrystalTop1;
   ModelRenderer CrystalTop2;

   public ModelCrystal() {
      this.textureWidth = 64;
      this.textureHeight = 32;
      this.CrystalMain = new ModelRenderer(this, 0, 0);
      this.CrystalMain.addBox(0.0F, 0.0F, 0.0F, 5, 7, 5);
      this.CrystalMain.setRotationPoint(-3.0F, 10.0F, -3.0F);
      this.CrystalMain.setTextureSize(64, 32);
      this.CrystalMain.mirror = true;
      this.setRotation(this.CrystalMain, 0.0F, 0.0F, 0.0F);
      this.CrystalEdge1 = new ModelRenderer(this, 0, 14);
      this.CrystalEdge1.addBox(0.0F, 0.0F, 0.0F, 3, 1, 3);
      this.CrystalEdge1.setRotationPoint(-2.0F, 9.0F, -2.0F);
      this.CrystalEdge1.setTextureSize(64, 32);
      this.CrystalEdge1.mirror = true;
      this.setRotation(this.CrystalEdge1, 0.0F, 0.0F, 0.0F);
      this.CrystalEdge2 = new ModelRenderer(this, 0, 14);
      this.CrystalEdge2.addBox(0.0F, 0.0F, 0.0F, 3, 1, 3);
      this.CrystalEdge2.setRotationPoint(-2.0F, 17.0F, -2.0F);
      this.CrystalEdge2.setTextureSize(64, 32);
      this.CrystalEdge2.mirror = true;
      this.setRotation(this.CrystalEdge2, 0.0F, 0.0F, 0.0F);
      this.CrystalTop1 = new ModelRenderer(this, 0, 12);
      this.CrystalTop1.addBox(0.0F, 8.0F, 0.0F, 1, 1, 1);
      this.CrystalTop1.setRotationPoint(-1.0F, 0.0F, -1.0F);
      this.CrystalTop1.setTextureSize(64, 32);
      this.CrystalTop1.mirror = true;
      this.setRotation(this.CrystalTop1, 0.0F, 0.0F, 0.0F);
      this.CrystalTop2 = new ModelRenderer(this, 0, 12);
      this.CrystalTop2.addBox(-1.0F, 18.0F, -1.0F, 1, 1, 1);
      this.CrystalTop2.setRotationPoint(0.0F, 0.0F, 0.0F);
      this.CrystalTop2.setTextureSize(64, 32);
      this.CrystalTop2.mirror = true;
      this.setRotation(this.CrystalTop2, 0.0F, 0.0F, 0.0F);
   }

   public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
      super.render(entity, f, f1, f2, f3, f4, f5);
      this.setRotationAngles(f, f1, f2, f3, f4, f5, entity);
      this.CrystalMain.render(f5);
      this.CrystalEdge1.render(f5);
      this.CrystalEdge2.render(f5);
      this.CrystalTop1.render(f5);
      this.CrystalTop2.render(f5);
   }

   private void setRotation(ModelRenderer model, float x, float y, float z) {
      model.rotateAngleX = x;
      model.rotateAngleY = y;
      model.rotateAngleZ = z;
   }

   public void setRotationAngles(float f, float f1, float f2, float f3, float f4, float f5, Entity entity) {
      super.setRotationAngles(f, f1, f2, f3, f4, f5, entity);
   }
}
