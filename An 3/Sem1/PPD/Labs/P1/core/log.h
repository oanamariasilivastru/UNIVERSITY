#pragma once

#include <iostream>  // Ensure this is here

#ifdef DISABLE_COUT

class _fakelog : public std::ostream
{
public:
    template<typename T>
    std::ostream& operator<<(const T&)
    {
        return *this;
    }

    _fakelog() : std::ostream(nullptr) {}
} fake_log;

std::ostream& my_log = fake_log;

#else
std::ostream& my_log = std::cout;
#endif
