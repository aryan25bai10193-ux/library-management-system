package com.vit.library.model;

import java.time.LocalDate;

public interface Renewable {

    LocalDate renew();

    boolean isRenewable();
}
