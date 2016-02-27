package k4unl.minecraft.Hydraulicraft.api;


public enum PressureTier {
	LOWPRESSURE(0),MEDIUMPRESSURE(1),HIGHPRESSURE(2), INVALID(-1);
	
	private final int tier;

	public static final PressureTier[] VALID_TIERS = {LOWPRESSURE, MEDIUMPRESSURE, HIGHPRESSURE};
	
	private PressureTier(int _tier){
		this.tier = _tier;
	}

	@Override
	public String toString(){
		return "TIER-" + tier;
	}
	
	public static PressureTier fromOrdinal(int tier){
		if(tier <= 2 && tier >= 0){
			return VALID_TIERS[tier];
		}else{
			return INVALID;
		}
	}

    public int toInt() {
        return this.tier;
    }
}
