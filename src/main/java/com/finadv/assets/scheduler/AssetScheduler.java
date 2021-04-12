package com.finadv.assets.scheduler;

import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.List;

import org.joda.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.finadv.assets.dto.UserAssetDto;
import com.finadv.assets.entities.PortfolioHistory;
import com.finadv.assets.repository.UserAssetRepository;
import com.finadv.assets.service.AssetService;

/**
 * @author hi
 *
 */
@Component
public class AssetScheduler {

	private static final Logger LOG = LoggerFactory.getLogger(AssetScheduler.class);
	private UserAssetRepository userAssetRepository;

	private AssetService assetService;

	@Autowired
	public void setUserAssetRepository(UserAssetRepository userAssetRepository) {
		this.userAssetRepository = userAssetRepository;
	}

	@Autowired
	public void setAssetService(AssetService assetService) {
		this.assetService = assetService;
	}

	// @Scheduled(initialDelay = 2000, fixedRate = 200000)
	@Scheduled(cron = "0 0 18 28 * ?")
	public void schedulePortfolioCapturePerMonth() {
		System.out.println("***********************************");

		final Calendar c = Calendar.getInstance();
		// if (c.get(Calendar.DATE) == c.getActualMaximum(Calendar.DATE)) {
		LOG.info("Inside schedulePortfolioCapturePerMonth at time : " + Instant.now());
		List<Long> userIdList = userAssetRepository.findDistinctUserId();
		for (Long uid : userIdList) {
			LOG.info("Method: schedulePortfolioCapturePerMonth : Getting assets for " + uid);
			try {
				UserAssetDto uaDto = assetService.getUserAssetByUserId(uid);

				// Get net worth
				double netWorth = uaDto.getAssets().stream().map(item -> item.getCurrentValue()).reduce(0.0,
						(a, b) -> a + b);
				LOG.info("Method: schedulePortfolioCapturePerMonth : Net worth of userId : " + uid + "  " + netWorth);

				PortfolioHistory portfolioHistory = new PortfolioHistory();
				portfolioHistory.setUserId(uid);
				portfolioHistory.setPeriod(LocalDateTime.now());
				portfolioHistory.setAmount(Double.valueOf(netWorth).longValue());

				assetService.saveUserPortfolioForPeriod(portfolioHistory);
			} catch (Exception e) {
				LOG.error(e.getMessage());
			}

		}

		// }

	}

}
