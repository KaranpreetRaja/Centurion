/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

#include "fdlibm.h"


#ifdef __STDC__
        double atanh(double x)          /* wrapper atanh */
#else
        double atanh(x)                 /* wrapper atanh */
        double x;
#endif
{
#ifdef _IEEE_LIBM
        return __ieee754_atanh(x);
#else
        double z,y;
        z = __ieee754_atanh(x);
        if(_LIB_VERSION == _IEEE_ || isnan(x)) return z;
        y = fabs(x);
        if(y>=1.0) {
            if(y>1.0)
                return __kernel_standard(x,x,30); /* atanh(|x|>1) */
            else
                return __kernel_standard(x,x,31); /* atanh(|x|==1) */
        } else
            return z;
#endif
}
