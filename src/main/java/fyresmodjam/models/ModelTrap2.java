package fyresmodjam.models;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelTrap2 extends ModelBase {
   ModelRenderer Shape1;
   ModelRenderer Shape22;
   ModelRenderer Shape26;
   ModelRenderer Shape30;
   ModelRenderer Shape27;

   public ModelTrap2() {
      this.textureWidth = 64;
      this.textureHeight = 32;
      this.Shape1 = new ModelRenderer(this, 0, 6);
      this.Shape1.addBox(0.0F, 0.0F, 0.0F, 14, 1, 14);
      this.Shape1.setRotationPoint(-7.0F, 23.0F, -7.0F);
      this.Shape1.setTextureSize(64, 32);
      this.Shape1.mirror = true;
      this.setRotation(this.Shape1, 0.0F, 0.0F, 0.0F);
      this.Shape22 = new ModelRenderer(this, 0, 0);
      this.Shape22.addBox(0.0F, 0.0F, 0.0F, 3, 2, 1);
      this.Shape22.setRotationPoint(-2.033333F, 21.0F, -2.0F);
      this.Shape22.setTextureSize(64, 32);
      this.Shape22.mirror = true;
      this.setRotation(this.Shape22, 0.0F, 0.0F, 0.0F);
      this.Shape26 = new ModelRenderer(this, 0, 0);
      this.Shape26.addBox(0.0F, 0.0F, 0.0F, 1, 2, 4);
      this.Shape26.setRotationPoint(1.0F, 21.0F, -2.0F);
      this.Shape26.setTextureSize(64, 32);
      this.Shape26.mirror = true;
      this.setRotation(this.Shape26, 0.0F, 0.0F, 0.0F);
      this.Shape30 = new ModelRenderer(this, 0, 0);
      this.Shape30.addBox(0.0F, 0.0F, 0.0F, 3, 2, 1);
      this.Shape30.setRotationPoint(-2.033333F, 21.0F, 1.0F);
      this.Shape30.setTextureSize(64, 32);
      this.Shape30.mirror = true;
      this.setRotation(this.Shape30, 0.0F, 0.0F, 0.0F);
      this.Shape27 = new ModelRenderer(this, 0, 0);
      this.Shape27.addBox(0.0F, 0.0F, 0.0F, 1, 2, 2);
      this.Shape27.setRotationPoint(-2.0F, 21.0F, -1.0F);
      this.Shape27.setTextureSize(64, 32);
      this.Shape27.mirror = true;
      this.setRotation(this.Shape27, 0.0F, 0.0F, 0.0F);
   }

   public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
      super.render(entity, f, f1, f2, f3, f4, f5);
      this.setRotationAngles(f, f1, f2, f3, f4, f5, entity);
      this.Shape1.render(f5);
      this.Shape22.render(f5);
      this.Shape26.render(f5);
      this.Shape30.render(f5);
      this.Shape27.render(f5);
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
