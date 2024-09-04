package cpu;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class GeneralPurposeRegisterTest {
	@Test
	public void	givenTwoPositiveRegisters_whenAddRegisters_returnAdded() {
		GeneralPurposeRegister R1 = new GeneralPurposeRegister("r1", 222);
		GeneralPurposeRegister R2 = new GeneralPurposeRegister("r2", 333);

		long excpectedValue = 555;
		long actualValue = R1.addRegisters(R2);

		assertEquals(excpectedValue, actualValue);
	}

	@Test
	public void	givenPositiveAndNegativeRegisters_whenAddRegisters_returnAdded() {
		GeneralPurposeRegister R1 = new GeneralPurposeRegister("r1", -222);
		GeneralPurposeRegister R2 = new GeneralPurposeRegister("r2", 333);

		long excpectedValue = 111;
		long actualValue = R1.addRegisters(R2);

		assertEquals(excpectedValue, actualValue);
	}

	@Test
	public void	givenTwoNegativeRegisters_whenAddRegisters_returnAdded() {
		GeneralPurposeRegister R1 = new GeneralPurposeRegister("r1", -222);
		GeneralPurposeRegister R2 = new GeneralPurposeRegister("r2", -333);

		long excpectedValue = -555;
		long actualValue = R1.addRegisters(R2);

		assertEquals(excpectedValue, actualValue);
	}

	@Test
	public void	givenTwoPositiveRegisters_whenSubtractRegisters_returnSubtracted() {
		GeneralPurposeRegister R1 = new GeneralPurposeRegister("r1", 222);
		GeneralPurposeRegister R2 = new GeneralPurposeRegister("r2", 333);

		long excpectedValue = -111;
		long actualValue = R1.subtractRegisters(R2);

		assertEquals(excpectedValue, actualValue);
	}

	@Test
	public void	givenPositiveAndNegativeRegisters_whenSubtractRegisters_returnSubtracted() {
		GeneralPurposeRegister R1 = new GeneralPurposeRegister("r1", -222);
		GeneralPurposeRegister R2 = new GeneralPurposeRegister("r2", 333);

		long excpectedValue = -555;
		long actualValue = R1.subtractRegisters(R2);

		assertEquals(excpectedValue, actualValue);
	}

	@Test
	public void	givenTwoNegativeRegisters_whenSubtractRegisters_returnSubtracted() {
		GeneralPurposeRegister R1 = new GeneralPurposeRegister("r1", -222);
		GeneralPurposeRegister R2 = new GeneralPurposeRegister("r2", -333);

		long excpectedValue = 111;
		long actualValue = R1.subtractRegisters(R2);

		assertEquals(excpectedValue, actualValue);
	}

	@Test
	public void	givenTwoPositiveRegisters_whenDivideRegisters_returnDivided() {
		GeneralPurposeRegister R1 = new GeneralPurposeRegister("r1", 222);
		GeneralPurposeRegister R2 = new GeneralPurposeRegister("r2", 333);

		long excpectedValue = 222/333;
		long actualValue = R1.divideRegisters(R2);

		assertEquals(excpectedValue, actualValue);
	}

	@Test
	public void	givenPositiveAndNegativeRegisters_whenDivideRegisters_returnDivided() {
		GeneralPurposeRegister R1 = new GeneralPurposeRegister("r1", -222);
		GeneralPurposeRegister R2 = new GeneralPurposeRegister("r2", 333);

		long excpectedValue = (-222)/333;
		long actualValue = R1.divideRegisters(R2);

		assertEquals(excpectedValue, actualValue);
	}

	@Test
	public void	givenTwoNegativeRegisters_whenDivideRegisters_returnDivided() {
		GeneralPurposeRegister R1 = new GeneralPurposeRegister("r1", -222);
		GeneralPurposeRegister R2 = new GeneralPurposeRegister("r2", -333);

		long excpectedValue = (-222)/(-333);
		long actualValue = R1.divideRegisters(R2);

		assertEquals(excpectedValue, actualValue);
	}

	@Test
	public void	givenTwoPositiveRegisters_whenMultiplyRegisters_returnMultiplied() {
		GeneralPurposeRegister R1 = new GeneralPurposeRegister("r1", 222);
		GeneralPurposeRegister R2 = new GeneralPurposeRegister("r2", 333);

		long excpectedValue = 222*333;
		long actualValue = R1.multiplyRegisters(R2);

		assertEquals(excpectedValue, actualValue);
	}

	@Test
	public void	givenPositiveAndNegativeRegisters_whenMultiplyRegisters_returnMultiplied() {
		GeneralPurposeRegister R1 = new GeneralPurposeRegister("r1", -222);
		GeneralPurposeRegister R2 = new GeneralPurposeRegister("r2", 333);

		long excpectedValue = (-222)*333;
		long actualValue = R1.multiplyRegisters(R2);

		assertEquals(excpectedValue, actualValue);
	}

	@Test
	public void	givenTwoNegativeRegisters_whenMultiplyRegisters_returnMultiplied() {
		GeneralPurposeRegister R1 = new GeneralPurposeRegister("r1", -222);
		GeneralPurposeRegister R2 = new GeneralPurposeRegister("r2", -333);

		long excpectedValue = (-222)*(-333);
		long actualValue = R1.multiplyRegisters(R2);

		assertEquals(excpectedValue, actualValue);
	}

	@Test
	public void	givenTwoRegisters_whenBitwiseANDRegisters_returnAND() {
		GeneralPurposeRegister R1 = new GeneralPurposeRegister("r1", 222);
		GeneralPurposeRegister R2 = new GeneralPurposeRegister("r2", 333);

		long excpectedValue = 222&333;
		long actualValue = R1.bitwiseANDRegisters(R2);

		assertEquals(excpectedValue, actualValue);
	}

	@Test
	public void	givenTwoRegisters_whenBitwiseORRegisters_returnOR() {
		GeneralPurposeRegister R1 = new GeneralPurposeRegister("r1", 222);
		GeneralPurposeRegister R2 = new GeneralPurposeRegister("r2", 333);

		long excpectedValue = 222|333;
		long actualValue = R1.bitwiseORRegisters(R2);

		assertEquals(excpectedValue, actualValue);
	}

	@Test
	public void	givenTwoRegisters_whenBitwiseXORRegisters_returnXOR() {
		GeneralPurposeRegister R1 = new GeneralPurposeRegister("r1", 222);
		GeneralPurposeRegister R2 = new GeneralPurposeRegister("r2", 333);

		long excpectedValue = 222^333;
		long actualValue = R1.bitwiseXORRegisters(R2);

		assertEquals(excpectedValue, actualValue);
	}

	@Test
	public void	givenOneRegister_whenBitwiseNOTRegister_returnNOT() {
		GeneralPurposeRegister R = new GeneralPurposeRegister("r1", 222);

		long excpectedValue = ~222;
		long actualValue = R.bitwiseNOTRegister();

		assertEquals(excpectedValue, actualValue);
	}
}
