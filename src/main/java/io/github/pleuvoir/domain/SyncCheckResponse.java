package io.github.pleuvoir.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SyncCheckResponse {
	private int retcode;
	private int selector;

}
