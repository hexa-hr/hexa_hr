package wage.model;

// 지난급여 불러오기 결과
public class WagePaymentPreviousCopyResult {

	private Integer workerEmployeeCount;
	private Integer businessEmployeeCount;
	private Integer copiedItemCount;
	private Integer deletedItemCount;

	public WagePaymentPreviousCopyResult(
		Integer workerEmployeeCount,
		Integer businessEmployeeCount,
		Integer copiedItemCount,
		Integer deletedItemCount) {

		this.workerEmployeeCount = workerEmployeeCount;
		this.businessEmployeeCount = businessEmployeeCount;
		this.copiedItemCount = copiedItemCount;
		this.deletedItemCount = deletedItemCount;
	}

	public Integer getWorkerEmployeeCount() {
		return workerEmployeeCount;
	}

	public Integer getBusinessEmployeeCount() {
		return businessEmployeeCount;
	}

	public Integer getCopiedItemCount() {
		return copiedItemCount;
	}

	public Integer getDeletedItemCount() {
		return deletedItemCount;
	}
}