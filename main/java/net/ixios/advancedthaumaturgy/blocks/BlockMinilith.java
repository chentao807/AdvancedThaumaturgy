package net.ixios.advancedthaumaturgy.blocks;

import java.util.List;

import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IEssentiaContainerItem;
import thaumcraft.api.crafting.ShapedArcaneRecipe;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.config.ConfigResearch;
import thaumcraft.common.items.ItemEssence;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.ixios.advancedthaumaturgy.AdvThaum;
import net.ixios.advancedthaumaturgy.items.TCItems;
import net.ixios.advancedthaumaturgy.misc.ATResearchItem;
import net.ixios.advancedthaumaturgy.tileentities.TileThaumicFertilizer;
import net.ixios.advancedthaumaturgy.tileentities.TileFluxDissipator;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;

public class BlockMinilith extends BlockContainer
{

	public static int blockID;
	public static int renderID;
	
    public BlockMinilith(int id, Material material)
    {
        super(id, material);
        renderID = RenderingRegistry.getNextAvailableRenderId();
        blockID = id;
        this.setCreativeTab(AdvThaum.tabAdvThaum);
        this.setHardness(1.0f);
        
    }

    @Override
    public void getSubBlocks(int par1, CreativeTabs par2CreativeTabs,
            List list)
    {
    	ItemStack base = new ItemStack(this, 1, 0);
    	list.add(base);
    	LanguageRegistry.addName(base, "at.inertminilith");
    	
    	ItemStack flux = new ItemStack(this, 1, 1); 
    	list.add(flux);
    	LanguageRegistry.addName(flux, "at.fluxdissipator");
    	
    }
    
    @Override
    public String getUnlocalizedName()
    {
        // TODO Auto-generated method stub
        return super.getUnlocalizedName();
    }
	 @Override
	 public TileEntity createTileEntity(World world, int metadata)
	 {
	 	return new TileFluxDissipator();
	 }
	 
    public void register()
    {
      /*  GameRegistry.registerBlock(this, "blockFluxDissipator");
        GameRegistry.registerTileEntity(TileFluxDissipator.class, "tileentityFluxDissipator");
        
        //GameRegistry.registerBlock(block, name);
        ShapedArcaneRecipe recipe = new ShapedArcaneRecipe("fluxdissipator", new ItemStack(this, 1, 0), 
        		new AspectList().add(Aspect.ENTROPY, 50), 
    	new Object[] {"   ",
        			  "   ",
        			  "TTT", 'T', TCItems.tile});
        
        ConfigResearch.recipes.put("fluxdissipator", recipe);
        
        
        ATResearchItem ri = new ATResearchItem("fluxdissipator", "BASICS", new AspectList(), 0, 0, 0, new ItemStack(this));
        
        ri.setTitle("Minilith");
        ri.setInfo("Minilith");
        ri.setParents("MERCURIALCORE");
        
       // ri.registerResearchItem();
*/        
    }
    
	@Override
 	public boolean renderAsNormalBlock() 
 	{
 		return false;
 	}
 	
 	@Override
 	public boolean isOpaqueCube() 
 	{
 		return false;
 	}
 	
     @Override
     public TileEntity createNewTileEntity(World world)
     {
    	 return null;
     }
	
 	@SideOnly(Side.CLIENT)
 	@Override
 	public void registerIcons(IconRegister ir)
 	{
 		blockIcon = ir.registerIcon("minecraft:obsidian");
 	}
 	
     @Override
     public int getRenderType()
     {
    	 return renderID;
     }
	 
	 @Override
	public boolean canRenderInPass(int pass)
	{
		return true;	
	}

	 @Override
	public int getRenderBlockPass()
	{
		return 1;
	}
	 
	 @Override
	 public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack stack)
	 {
		 ForgeDirection direction  = ForgeDirection.UNKNOWN;
		 TileFluxDissipator te = (TileFluxDissipator)world.getBlockTileEntity(x,  y,  z);
		 super.onBlockPlacedBy(world, x, y, z, entity, stack);
	 }
	 
	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX,
	        float hitY, float hitZ)
	{
	    ItemStack helditem = player.getHeldItem();
	    TileEntity te = world.getBlockTileEntity(x,  y,  z);
	    
	    if (helditem == null || !(helditem.getItem() instanceof IEssentiaContainerItem))
	    	return false;
	 
	    if (!(te instanceof TileFluxDissipator))
	    	return false;
	    
	    TileFluxDissipator fd = (TileFluxDissipator)te;
	 
	    if (helditem.getItemDamage() == 0)
	    {
	         if (fd.doesContainerContainAmount(Aspect.TAINT, 8)) 
	         {
	            if (world.isRemote) 
	            {
	               player.swingItem();
	               return false;
	            }

	            if (fd.takeFromContainer(Aspect.TAINT, 8))
	            {
	               --helditem.stackSize;
	               ItemStack stack = new ItemStack(ConfigItems.itemEssence, 1, 1);
	               ((ItemEssence)helditem.getItem()).setAspects(stack, (new AspectList()).add(Aspect.TAINT, 8));
	               if (!player.inventory.addItemStackToInventory(stack)) 
	               {
	                  world.spawnEntityInWorld(new EntityItem(world, (double)((float)x + 0.5F), (double)((float)y + 0.5F), (double)((float)z + 0.5F), stack));
	               }

	               world.playSoundAtEntity(player, "liquid.swim", 0.25F, 1.0F);
	               player.inventoryContainer.detectAndSendChanges();
	               return true;
	            }
	         }
	      }
	    
	    return false;
	}
}
